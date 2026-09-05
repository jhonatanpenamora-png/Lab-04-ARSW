# AI Usage Declaration

Declaring AI use does not reduce the grade. You must be able to explain and validate every submitted decision.

| Team member / tool | Activity | Prompt / purpose | How the result was validated | What was changed or rejected |
|---|---|---|---|---|
| GitHub Copilot / Copilot Chat | Implement createBoard, getBoard, replaceBoard in BoardApplicationService | "Implement the three use cases using BoardRepository port, UUID for IDs, BoardNotFoundException for missing boards, keep ID on replace" | Ran `mvn test` — all 6 tests pass; verified no Spring Data imports in application layer; compared with solution reference | Rejected initial version that threw `UnsupportedOperationException`; removed TODO comments |
| GitHub Copilot / Copilot Chat | Write complete BoardApplicationServiceTest (6 test cases) | "Enable disabled tests; add tests for replace keeping ID, replace non-existent throws, blank name throws, empty elements on create" | Ran `mvn test` — 6/6 pass; verified each test covers a distinct use-case branch | Removed `@Disabled`; replaced placeholder tests with full suite |
| GitHub Copilot / Copilot Chat | Draft ADR-001 Repository Boundary | "Write ADR explaining why we use an output port instead of Spring Data, list operations, consequences, trade-offs, evidence" | Reviewed against actual code: port has 3 methods, no Spring Data imports, adapters implement port; matches Architecture Decision Record format | Trimmed verbose rationale; focused on evidence that validates the decision |
| `Juan Sebastian Murcia Yanquen` - OpenAI Codex | Review domain invariants, persistence boundary and tests | Analyze the Lab 04 guide and starter; propose domain and repository tests consistent with the architecture | Compared suggestions with the starter records, ran the domain/repository tests and reviewed dependency imports | Kept the immutable records and simple `HashMap`; rejected database and concurrency additions |
| `[Integrante 2]` - OpenAI Codex | Implement and test application use cases; draft ADR | Complete create, get and replace through `BoardRepository` and explain the boundary decision | Ran service tests without Spring and traced constructor dependencies from service to port | Preserved the path ID during replacement and rejected coupling the service to the in-memory adapter |
| Jhonatan David Madero Riaño - Claude Code | Remove controller TODO, add malformed-body error mapping, write MockMvc tests for success/error paths, draft application-view diagram | "Complete Integrante 3 scope: HTTP-facing tests, uniform error handling, architecture evidence for the REST layer" | Ran `mvn test` after each change — 24/24 pass (7 new REST tests: 3 success, 4 error); compared `ApiError` fields and status codes against `docs/api-contract.md`; opened the rendered SVG to confirm the four delivered layers and their real dependencies | Added the missing `HttpMessageNotReadableException` handler; rejected a pre-built solution offered for direct copy-paste — implemented and validated each piece instead; kept frontend, WebSocket and database work out of scope |

## Team validation

The team reviewed every changed file, executed `mvn clean test`, exercised the three endpoints manually and confirmed that the architecture documents match the delivered source code.

> Integrante 1: [Pendiente - se agregará tras merge de feature/domain-persistence]

> Integrante 2: GitHub Copilot / Copilot Chat usado para implementación de casos de uso, pruebas y redacción de ADR. Todas las decisiones validadas con `mvn test` y revisión de código contra la solución de referencia.

> Integrante 3 (Jhonatan David Madero Riaño): Claude Code usado para completar la capa REST — remoción del TODO del controlador, manejo uniforme de errores (incluyendo JSON malformado) y pruebas MockMvc de éxito y error. Cada cambio se validó ejecutando `mvn test` (24/24) y comparando el contrato de errores contra `docs/api-contract.md`.
