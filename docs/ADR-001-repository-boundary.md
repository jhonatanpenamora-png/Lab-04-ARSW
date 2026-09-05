# ADR-001 — Repository Boundary

## Status
Accepted

## Context
The application layer defines use cases (create, get, replace boards) that require persistence. The domain model (`Board`, `BoardElement`) must remain free of infrastructure concerns. Spring Data JPA repositories leak framework abstractions into the application layer and couple the domain to a specific persistence technology. We need a clean boundary that allows swapping the storage implementation (in-memory, database, external service) without changing use cases or domain code.

## Decision
Define an output port `BoardRepository` owned by the application layer with the minimal operations required by the use cases:
- `save(Board)` — persist or update a board
- `findById(String)` — retrieve a board by identity
- `existsById(String)` — check existence without loading the aggregate

The port resides in `application.port.out` and uses only domain types and `java.util.Optional`. No Spring Data interfaces (`CrudRepository`, `JpaRepository`) or annotations appear in this layer. The in-memory adapter (`InMemoryBoardRepository`) implements this port using a `HashMap`. Future adapters (JPA, MongoDB, Redis, HTTP client) implement the same interface.

## Positive consequences
- Use cases depend on a stable abstraction, not a framework.
- Unit tests run fast with a fake or in-memory adapter; no container or database required.
- Swapping storage (e.g., to PostgreSQL) only requires a new adapter; use cases and domain remain untouched.
- The port surface is minimal, reducing the cost of new adapters.

## Trade-off
- Manual implementation of each adapter vs. Spring Data's auto-generated queries.
- No built-in pagination, sorting, or query derivation; these are added to the port only when a use case needs them.

## Evidence / validation
- `BoardApplicationService` compiles and tests pass using only `BoardRepository` (no Spring Data imports).
- `InMemoryBoardRepositoryTest` verifies save/replace semantics and `Optional` behavior.
- `BoardApplicationServiceTest` exercises all three use cases against the port.
- No `import org.springframework.data` in `application.service` or `domain.model`.