# AI Usage Declaration

Declaring AI use does not reduce the grade. You must be able to explain and validate every submitted decision.

| Tool | Activity | Prompt / purpose | How I validated the result | What I changed / rejected |
|---|---|---|---|---|
| GitHub Copilot / Copilot Chat | Implement createBoard, getBoard, replaceBoard in BoardApplicationService | "Implement the three use cases using BoardRepository port, UUID for IDs, BoardNotFoundException for missing boards, keep ID on replace" | Ran `mvn test` — all 6 tests pass; verified no Spring Data imports in application layer; compared with solution reference | Rejected initial version that threw `UnsupportedOperationException`; removed TODO comments |
| GitHub Copilot / Copilot Chat | Write complete BoardApplicationServiceTest (6 test cases) | "Enable disabled tests; add tests for replace keeping ID, replace non-existent throws, blank name throws, empty elements on create" | Ran `mvn test` — 6/6 pass; verified each test covers a distinct use-case branch | Removed `@Disabled`; replaced placeholder tests with full suite |
| GitHub Copilot / Copilot Chat | Draft ADR-001 Repository Boundary | "Write ADR explaining why we use an output port instead of Spring Data, list operations, consequences, trade-offs, evidence" | Reviewed against actual code: port has 3 methods, no Spring Data imports, adapters implement port; matches Architecture Decision Record format | Trimmed verbose rationale; focused on evidence that validates the decision |

If no AI tool was used, state it explicitly below:

> Integrante 1: [Pendiente - se agregará tras merge de feature/domain-persistence]
> Integrante 2: GitHub Copilot / Copilot Chat usado para implementación de casos de uso, pruebas y redacción de ADR. Todas las decisiones validadas con `mvn test` y revisión de código contra la solución de referencia.
> Integrante 3: [Pendiente - se agregará tras merge de feature/rest-api]