# REST Contract - Lab 04

Base URL: `http://localhost:8080/api/boards`

All request and response bodies use `application/json`.

| Method | Resource | Request | Success response | Error cases |
|---|---|---|---|---|
| POST | `/api/boards` | `CreateBoardRequest` | `201 Created` + created `Board` | `400` invalid body or blank name |
| GET | `/api/boards/{boardId}` | None | `200 OK` + existing `Board` | `404` board does not exist |
| PUT | `/api/boards/{boardId}` | `ReplaceBoardRequest` | `200 OK` + replaced `Board` | `400` invalid body; `404` board does not exist |

## Board representation

```json
{
  "id": "7eb08f5a-e34f-4e17-8e70-c8212d7d483e",
  "name": "Architecture Board",
  "elements": [
    {
      "id": "element-1",
      "type": "RECTANGLE",
      "x": 40.0,
      "y": 60.0,
      "width": 180.0,
      "height": 100.0,
      "text": "",
      "sourceId": null,
      "targetId": null
    },
    {
      "id": "element-2",
      "type": "TEXT",
      "x": 260.0,
      "y": 80.0,
      "width": 160.0,
      "height": 40.0,
      "text": "Application Service",
      "sourceId": null,
      "targetId": null
    },
    {
      "id": "connector-1",
      "type": "CONNECTOR",
      "x": 0.0,
      "y": 0.0,
      "width": 0.0,
      "height": 0.0,
      "text": "",
      "sourceId": "element-1",
      "targetId": "element-2"
    }
  ]
}
```

### `BoardElement` fields

| Field | Type | Notes |
|---|---|---|
| `id` | `String` | Required, non-blank; unique within the board. |
| `type` | `ElementType` | `RECTANGLE`, `TEXT` or `CONNECTOR`. |
| `x`, `y` | `double` | Position on the canvas. |
| `width`, `height` | `double` | Cannot be negative. |
| `text` | `String` | Defaults to `""` when omitted. |
| `sourceId`, `targetId` | `String` or `null` | Only meaningful for `CONNECTOR` elements (see invariants below); forced to `null` for `RECTANGLE`/`TEXT` regardless of what the client sends. |

### Connector invariants

Enforced in the domain model, not in persistence (`BoardElement` compact constructor and `Board.validateConnectors`):

- A `CONNECTOR` element must have both `sourceId` and `targetId` non-null and non-blank.
- `sourceId` and `targetId` must be different from each other.
- Both must reference element `id`s that exist elsewhere in the same board's `elements` list.
- Neither endpoint may itself be a `CONNECTOR` (a connector cannot link to another connector).
- Any violation is a domain invariant violation and is reported as `400 Bad Request` / `INVALID_INPUT` (see [Error contract](#error-contract)).

## Create Board

```http
POST /api/boards
Content-Type: application/json
```

```json
{
  "name": "Architecture Board"
}
```

The server generates the Board identifier and starts it with an empty element collection. A successful request returns `201 Created` and the created Board.

## Get Board

```http
GET /api/boards/7eb08f5a-e34f-4e17-8e70-c8212d7d483e
```

A successful request returns `200 OK` and the current Board representation.

## Replace Board state

```http
PUT /api/boards/7eb08f5a-e34f-4e17-8e70-c8212d7d483e
Content-Type: application/json
```

```json
{
  "name": "Updated Architecture Board",
  "elements": [
    {
      "id": "element-1",
      "type": "TEXT",
      "x": 20.0,
      "y": 30.0,
      "width": 160.0,
      "height": 40.0,
      "text": "Application Service"
    }
  ]
}
```

The path identifier is authoritative. The operation replaces the name and complete element collection while retaining that identifier. It only operates on an existing Board; it does not perform an upsert. Success returns `200 OK` and the replaced Board.

## Error contract

Every mapped error exposes the same five fields:

```json
{
  "timestamp": "2026-09-02T20:00:00Z",
  "status": 404,
  "code": "BOARD_NOT_FOUND",
  "message": "Board not found: missing-board",
  "path": "/api/boards/missing-board"
}
```

| HTTP status | Code | Meaning |
|---|---|---|
| `400 Bad Request` | `INVALID_REQUEST` | Bean Validation failure, malformed JSON or invalid element representation |
| `400 Bad Request` | `INVALID_INPUT` | Domain invariant violation |
| `404 Not Found` | `BOARD_NOT_FOUND` | No Board exists with the requested ID |

The error contract is independent of concrete Java exception names, so internal implementation changes do not break clients.

## Scope notes (Lab 5)

Lab 5 adds the `CONNECTOR` element type and its invariants (see above), consumed by the interactive web client under `src/main/resources/static/`. The REST contract itself did not change: `POST /api/boards`, `GET /api/boards/{boardId}` and `PUT /api/boards/{boardId}` are the same three operations, with the same status codes (`201`/`400` on create, `200`/`404` on get, `200`/`400`/`404` on replace) described above — `PUT` still fully replaces the element collection, it does not merge or patch individual elements. No `/move`, `/draw` or autosave endpoints were added, and no WebSocket channel exists; every client mutation (move, connect, delete) stays local until the client issues an explicit `PUT`.
