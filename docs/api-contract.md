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
      "text": ""
    }
  ]
}
```

Supported element types are `RECTANGLE` and `TEXT`. Element dimensions cannot be negative.

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
