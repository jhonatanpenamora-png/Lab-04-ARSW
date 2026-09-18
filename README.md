# ARSW Collaborative Architecture Board — Lab 04 Starter

This repository is the starting point for **Lab #4 — Architecture Foundation**.

The goal is **not** to practice REST syntax. The goal is to build a small backend with explicit architectural boundaries, dependency inversion, constructor injection, consistent error handling, tests, and architecture evidence.

## Technology baseline

- Java 21
- Spring Boot 3.x
- Maven
- In-memory persistence for this lab

## Target architecture

```text
REST Controller
      |
      v
Application Service
      |
      v
BoardRepository (port)
      |
      v
InMemoryBoardRepository (adapter)
```

## What is already provided

- Project and package structure.
- Domain types: `Board`, `BoardElement`, `ElementType`.
- Persistence port and in-memory adapter shell.
- Application service shell.
- REST controller shell.
- Central error contract.
- Documentation templates.
- Disabled tests that describe expected behavior.

## What you must complete

Search for `TODO LAB-04` in the repository.

At minimum, complete:

1. `BoardRepository` operations required by the use cases.
2. `InMemoryBoardRepository` behavior.
3. `BoardApplicationService` use cases.
4. REST request validation and controller behavior.
5. Consistent error mapping.
6. Unit and HTTP-facing tests.
7. `docs/api-contract.md`.
8. Architecture diagrams in `docs/architecture/`.
9. `docs/ADR-001-repository-boundary.md`.
10. `docs/AI_USAGE.md`.

## Run

```bash
mvn clean test
mvn spring-boot:run
```

Then open the interactive board UI at:

```text
http://localhost:8080/
```

`mvn spring-boot:run` serves the Lab 5 client (`src/main/resources/static/index.html` + `js/app.js`): create or load a board, add rectangles/text, drag them, connect two elements, and save/reload against the REST API below.

## Verify

```bash
mvn test
```

## Evidence

`docs/evidence/lab-05-save-reload.png` / `.gif` show a full Lab 5 round trip in the browser: a board with an id, a rectangle, a text element and a connector between them, a successful save, and the same state after reloading the page and loading the board by id.

## Try it manually

With the application running (`mvn spring-boot:run`):

```bash
# Create a board
curl -i -X POST http://localhost:8080/api/boards \
  -H "Content-Type: application/json" \
  -d '{"name":"Architecture Board"}'

# Get it back (replace ID with the one returned above)
curl -i http://localhost:8080/api/boards/ID

# Replace its state
curl -i -X PUT http://localhost:8080/api/boards/ID \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Board","elements":[{"id":"element-1","type":"TEXT","x":20,"y":30,"width":160,"height":40,"text":"Application Service"}]}'

# Unknown board -> uniform 404
curl -i http://localhost:8080/api/boards/no-existe
```

## Continuity rule

Your completed Lab 04 repository becomes the conceptual baseline for **Lab 05 — Interactive Board**. Avoid unnecessary changes to contracts and package boundaries.
