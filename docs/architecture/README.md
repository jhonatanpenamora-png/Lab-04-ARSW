# Architecture Evidence — Lab 04 / Lab 05

Both diagrams use ArchiMate notation (`«Application Component»`, `«Application Interface»`, `«Data Object»`, `«Business Actor»`) rendered as Mermaid flowcharts, per the course requirement that architecture views be expressed in ArchiMate rather than plain UML.

## 1. Application view

`application-view.mmd` (source) and `application-view.svg` (rendered) show the layers actually delivered, now including the Lab 5 client:

- **Client (browser)** — `BoardApp` (coordinates events and use cases), `BoardApiClient` (the only module that calls `fetch`), `BoardState` (holds the `Board`, selection and connect-mode state) and `BoardView` (projects state onto the SVG canvas and reports interface gestures back to `BoardApp`). See `ADR-002-client-boundaries.md` for why these four are kept separate.
- **Web layer** — `BoardRestController` (REST interface) and `GlobalExceptionHandler` (uniform `ApiError` mapping).
- **Application layer** — `BoardApplicationService` (use cases) depending only on the `BoardRepository` port.
- **Persistence adapter** — `InMemoryBoardRepository`, implementing the port over an in-memory map.

The relationships mirror the dependency-inversion boundary from `ADR-001-repository-boundary.md`: the application layer never points at the adapter, only at the port. On the client side, `BoardApiClient` is the only edge crossing into the REST layer — `BoardView` never talks to the network, and `BoardState` never talks to the DOM.

## 2. Class / module view

`class-diagram.mmd` / `class-diagram.svg` model the same components as ArchiMate `«Application Component»` boxes (`BoardApp`, `BoardApiClient`, `BoardState`, `BoardView`, `BoardRestController`, `BoardApplicationService`, `InMemoryBoardRepository`), the `BoardRepository` port as an `«Application Interface»`, and the domain records (`Board`, `BoardElement`, `ElementType` — including the Lab 5 `CONNECTOR` type and its `sourceId`/`targetId` fields) as `«Data Object»`s. Key fields and method signatures are kept inside each node label so the ArchiMate view doesn't lose the property-level detail the domain model needs. `Adapter -.->|"realizes"| Port` is the ArchiMate equivalent of "implements": the dependency still points from the service toward the port, never toward the in-memory adapter.

## Quality rule

The diagrams describe the code that is actually delivered. No decorative boxes, no generated diagrams listing every framework class.
