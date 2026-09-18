# ADR-002 — Client Responsibility Boundaries

## Status
Accepted

## Context
Lab 5 adds an interactive SVG client on top of the Lab 4 REST API: users create or load a board, add shapes, move them, connect them, and persist changes back to the server. A single-file or ad-hoc client for this kind of surface tends to accrete four concerns at once — issuing HTTP requests, holding the current board/selection/interaction state, coordinating what happens in response to a user action (including concurrency and retry), and manipulating the DOM/SVG. When those concerns live in the same functions, a change to one (for example, how a failed request is retried) risks touching unrelated code (for example, how a connector line is drawn), and testing any single concern in isolation becomes hard because it drags fetch mocks, DOM fixtures, and state assertions together. We need an explicit boundary so each concern can change, and be tested, independently — especially since Lab 6 will add WebSocket collaboration on top of this same client.

## Decision
Split the client into four modules with non-overlapping responsibilities:

- **`BoardApp`** (`src/main/resources/static/js/app.js`) coordinates user-triggered events and use cases. It wires the other three modules together, owns the concurrency/retry policy for remote operations (`executeRemote`, line 53), and decides when a local mutation is allowed (`canEdit()`, line 69).
- **`BoardApiClient`** (`src/main/resources/static/js/api/board-api-client.js`) is the only module that calls `fetch`. It validates request inputs, builds the HTTP request, and translates every response — success or failure — into either a plain `Board` payload or a typed `BoardApiError` (`create` line 44, `load` line 53, `save` line 58, via the shared `request`/`parseResponse` wrappers at lines 32 and 17).
- **`BoardState`** (`src/main/resources/static/js/state/board-state.js`) holds the current `Board`, the current selection, the connect-mode interaction (`interaction.mode` / `interaction.sourceId`), and the remote status (`remote.status` / `operation` / `error`). All mutations (`move`, `beginConnect`, `completeConnect`, `removeSelected`, …) return new immutable snapshots; nothing else keeps its own copy of this data.
- **`BoardView`** (`src/main/resources/static/js/ui/board-view.js`) projects a `BoardState` snapshot onto the SVG canvas (`render(snapshot)`, line 82) and reports raw interface gestures (pointer down/move/up) back through a `handlers` callback object (`select`, `move`, `connectTarget`). It never imports `BoardApiClient` or `BoardState`, and never calls `fetch`.

## Positive consequences
- A bug in retry/concurrency handling is fixed in `app.js` only; the fix cannot leak into how a connector is rendered or how a `PUT` request is built.
- `BoardApiClient` can be tested, or replaced, with a fetch mock — independent of the DOM.
- `BoardView` can be tested, or replaced, by feeding it snapshots — independent of the network.
- `BoardState` can be tested as pure data transformations with no DOM or `fetch` involved.

## Trade-off
- More files and more internal contracts (snapshot shape, `handlers` callback shape, `BoardApiError` shape) than a single script would need.
- Every remote action passes through one extra layer of indirection (`BoardApp` → `BoardApiClient` → `fetch`) instead of calling `fetch` directly where the button handler lives.
- Accepted because the alternative — DOM handlers calling `fetch` directly and mutating state inline — makes HTTP, state and DOM impossible to change or test independently, which gets worse, not better, as the client grows.

## Evidence / validation
- `board-api-client.js` is the only file under `src/main/resources/static/js/` that references `fetch`; `parseResponse()` (line 17) and `request()` (line 32) are the single place an HTTP response becomes either a `Board` or a `BoardApiError`.
- `board-view.js` never imports `board-api-client.js` or `board-state.js` — its public surface is only `render(snapshot)` and `on(handlers)`.
- `board-state.js` mutations are immutable (`{...board, elements: [...]}` spreads; `snapshot()` returns a `structuredClone`), so `BoardApp` and `BoardView` never share a mutable reference to the board.

**End-to-end example — saving a board (`Save` button):**

1. `app.js` wires the Save button to `executeRemote('Guardando', () => BoardApiClient.save(state.toPersistedBoard()))`.
2. `executeRemote` (`app.js`, line 53) calls `state.setRemote('loading', 'Guardando')` and `refresh()`, so `BoardView.render(snapshot)` shows the canvas disabled (`canvas-disabled` class, driven by `snapshot.remote.status === 'loading'`) while the request is in flight.
3. `BoardState.toPersistedBoard()` converts the in-memory board into the `{id, name, elements}` shape the API expects.
4. `BoardApiClient.save(board)` (line 58) validates the payload and issues `PUT /api/boards/${id}` with `{name, elements: board.elements}` — the only `fetch` call in the whole operation.
5. On success, `executeRemote` calls `state.setBoard(board)` (a new immutable snapshot) and `state.setRemote('success', 'Guardando')`. On failure it calls `state.setRemote('error', 'Guardando', error)` and keeps a `retryOperation` closure so the Retry button re-runs step 1 unchanged.
6. `app.js` calls `refresh()` again, the only place `BoardView.render(snapshot)` is invoked — `BoardView` never knows whether the snapshot it just drew came from a save, a load, or a local edit.

This shows the four modules cooperating without any one absorbing another's responsibility: `BoardApp` decides *when*, `BoardApiClient` decides *how to talk to the server*, `BoardState` decides *what the truth is*, and `BoardView` decides *what it looks like*.
