# AI Usage Declaration

Declaring AI use does not reduce the grade. You must be able to explain and validate every submitted decision.

| Team member / tool | Activity | Prompt / purpose | How the result was validated | What was changed or rejected |
|---|---|---|---|---|
| `[Integrante 1]` - OpenAI Codex | Review domain invariants, persistence boundary and tests | Analyze the Lab 04 guide and starter; propose domain and repository tests consistent with the architecture | Compared suggestions with the starter records, ran the domain/repository tests and reviewed dependency imports | Kept the immutable records and simple `HashMap`; rejected database and concurrency additions |
| `[Integrante 2]` - OpenAI Codex | Implement and test application use cases; draft ADR | Complete create, get and replace through `BoardRepository` and explain the boundary decision | Ran service tests without Spring and traced constructor dependencies from service to port | Preserved the path ID during replacement and rejected coupling the service to the in-memory adapter |
| `[Integrante 3]` - OpenAI Codex | Complete REST tests, error handling, API contract and diagrams | Validate POST/GET/PUT responses, uniform errors and architecture evidence | Ran MockMvc tests, compared JSON examples with Java records and checked diagrams against code | Added malformed-body mapping; rejected frontend, WebSocket and database work as out of scope |

## Team validation

The team reviewed every changed file, executed `mvn clean test`, exercised the three endpoints manually and confirmed that the architecture documents match the delivered source code.

> TODO
