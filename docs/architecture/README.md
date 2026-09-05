# Architecture Evidence — Lab 04

## 1. Application view

`application-view.mmd` (source) and `application-view.svg` (rendered) show the four layers actually delivered:

- **Web layer** — `BoardRestController` (REST interface) and `GlobalExceptionHandler` (uniform `ApiError` mapping).
- **Application layer** — `BoardApplicationService` (use cases) depending only on the `BoardRepository` port.
- **Persistence adapter** — `InMemoryBoardRepository`, implementing the port over an in-memory map.
- **Client** — any HTTP client calling `/api/boards`.

The relationships mirror the dependency-inversion boundary from `ADR-001-repository-boundary.md`: the application layer never points at the adapter, only at the port.

## 2. Class diagram

`class-diagram.mmd` / `class-diagram.svg` cover `BoardRestController`, `BoardApplicationService`, `BoardRepository`, `InMemoryBoardRepository`, and the domain model (`Board`, `BoardElement`, `ElementType`) actually used by those classes.

## Quality rule

The diagrams describe the code that is actually delivered. No decorative boxes, no generated diagrams listing every framework class.
