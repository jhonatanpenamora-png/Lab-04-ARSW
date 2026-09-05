# AI Usage Declaration

Declaring AI use does not reduce the grade. You must be able to explain and validate every submitted decision.

| Team member / tool | Activity | Prompt / purpose | How the result was validated | What was changed or rejected |
|---|---|---|---|---|
| GitHub Copilot / Copilot Chat | Implement createBoard, getBoard, replaceBoard in BoardApplicationService | "Implement the three use cases using BoardRepository port, UUID for IDs, BoardNotFoundException for missing boards, keep ID on replace" | Ran `mvn test` — all 6 tests pass; verified no Spring Data imports in application layer; compared with solution reference | Rejected initial version that threw `UnsupportedOperationException`; removed TODO comments |
| GitHub Copilot / Copilot Chat | Write complete BoardApplicationServiceTest (6 test cases) | "Enable disabled tests; add tests for replace keeping ID, replace non-existent throws, blank name throws, empty elements on create" | Ran `mvn test` — 6/6 pass; verified each test covers a distinct use-case branch | Removed `@Disabled`; replaced placeholder tests with full suite |
| GitHub Copilot / Copilot Chat | Draft ADR-001 Repository Boundary | "Write ADR explaining why we use an output port instead of Spring Data, list operations, consequences, trade-offs, evidence" | Reviewed against actual code: port has 3 methods, no Spring Data imports, adapters implement port; matches Architecture Decision Record format | Trimmed verbose rationale; focused on evidence that validates the decision |
| `[Integrante 1]` - OpenAI Codex | Review domain invariants, persistence boundary and tests | Analyze the Lab 04 guide and starter; propose domain and repository tests consistent with the architecture | Compared suggestions with the starter records, ran the domain/repository tests and reviewed dependency imports | Kept the immutable records and simple `HashMap`; rejected database and concurrency additions |
| `[Integrante 2]` - OpenAI Codex | Implement and test application use cases; draft ADR | Complete create, get and replace through `BoardRepository` and explain the boundary decision | Ran service tests without Spring and traced constructor dependencies from service to port | Preserved the path ID during replacement and rejected coupling the service to the in-memory adapter |
| `[Integrante 3]` - OpenAI Codex | Complete REST tests, error handling, API contract and diagrams | Validate POST/GET/PUT responses, uniform errors and architecture evidence | Ran MockMvc tests, compared JSON examples with Java records and checked diagrams against code | Added malformed-body mapping; rejected frontend, WebSocket and database work as out of scope |

## Team validation

The team reviewed every changed file, executed `mvn clean test`, exercised the three endpoints manually and confirmed that the architecture documents match the delivered source code.

> Integrante 1: [Pendiente - se agregará tras merge de feature/domain-persistence]
> Integrante 2: GitHub Copilot / Copilot Chat usado para implementación de casos de uso, pruebas y redacción de ADR. Todas las decisiones validadas con `mvn test` y revisión de código contra la solución de referencia.
> Integrante 3: [Pendiente - se agregará tras merge de feature/rest-api]