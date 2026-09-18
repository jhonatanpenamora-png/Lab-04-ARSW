# Lab 5 — trabajo pendiente del integrante 3

Este documento comienza donde terminan los commits de dominio y cliente web. El integrante 3 debe completar la evidencia arquitectónica y la validación final sin cambiar las responsabilidades ya implementadas.

## Estado recibido

- El backend conserva la arquitectura del Lab 4.
- `BoardElement` soporta `RECTANGLE`, `TEXT` y `CONNECTOR`.
- Los conectores requieren `sourceId` y `targetId` distintos, existentes y no conectores.
- El cliente está separado en `BoardApp`, `BoardApiClient`, `BoardState` y `BoardView`.
- La aplicación permite crear/cargar, agregar, seleccionar, mover, conectar, eliminar, guardar y reintentar operaciones REST.
- Las acciones incompatibles quedan deshabilitadas mientras hay una operación remota en progreso.

## Commit 1 — ADR de límites del cliente

Crear `docs/ADR-002-client-boundaries.md`. No reemplazar ni renombrar `ADR-001`.

El ADR debe contener como mínimo:

- **Context:** por qué un cliente SVG interactivo puede mezclar HTTP, estado, coordinación y DOM si no se separan responsabilidades.
- **Decision:**
  - `BoardApp` coordina eventos y casos de uso del cliente.
  - `BoardApiClient` es el único módulo que utiliza `fetch` y traduce respuestas HTTP.
  - `BoardState` mantiene Board, selección, modo de conexión y estados remotos.
  - `BoardView` proyecta el estado sobre SVG y comunica eventos de interfaz.
- **Consequences:** cambios y pruebas más localizados; mayor cantidad de módulos y contratos internos.
- **Trade-off:** se acepta un poco más de estructura a cambio de evitar acoplamiento entre DOM, HTTP y estado.
- **Evidence:** enlazar los cuatro archivos implementados y mencionar una operación completa, por ejemplo cargar o guardar.

Commit sugerido:

```bash
git add docs/ADR-002-client-boundaries.md
git commit -m "docs(adr): document client responsibility boundaries"
```

## Commit 2 — contrato REST y diagramas

### Contrato REST

Actualizar `docs/api-contract.md` con:

- `POST /api/boards` con ejemplo de petición, respuesta `201` y error `400`.
- `GET /api/boards/{boardId}` con respuesta `200` y error `404`.
- `PUT /api/boards/{boardId}` indicando que reemplaza el estado completo.
- Ejemplo completo con un `RECTANGLE`, un `TEXT` y un `CONNECTOR`.
- Campos de `BoardElement`: `id`, `type`, `x`, `y`, `width`, `height`, `text`, `sourceId`, `targetId`.
- Invariantes del conector: extremos obligatorios, distintos, existentes y de tipo diferente a `CONNECTOR`.
- Formato uniforme de error: `timestamp`, `status`, `code`, `message`, `path`.
- Aclaración de que no se agregaron endpoints `/move`, `/draw`, autosave ni WebSockets.

### Vista de aplicación ArchiMate

Actualizar `docs/architecture/application-view.mmd` y regenerar `application-view.svg`.

La vista debe mostrar, con tipos o estereotipos claros:

1. Usuario usando la aplicación web.
2. Cliente web con `BoardApp`, `BoardApiClient`, `BoardState` y `BoardView`.
3. `BoardView` usando SVG/DOM.
4. `BoardApiClient` consumiendo la API REST.
5. API REST delegando en `BoardApplicationService`.
6. Servicio dependiendo del puerto `BoardRepository`.
7. Adaptador `InMemoryBoardRepository` implementando el puerto.

### Diagrama de clases/módulos

Actualizar `docs/architecture/class-diagram.mmd` y regenerar `class-diagram.svg`.

Debe incluir:

- `BoardApp`, `BoardApiClient`, `BoardState`, `BoardView` y sus dependencias.
- `BoardRestController`, `BoardApplicationService`, `BoardRepository` e `InMemoryBoardRepository`.
- `Board`, `BoardElement` y `ElementType` con `CONNECTOR`, `sourceId` y `targetId`.
- La dependencia debe apuntar desde la política hacia la abstracción: el servicio depende de `BoardRepository`, no del repositorio en memoria.

Actualizar también `docs/architecture/README.md` para explicar ambos diagramas.

Commit sugerido:

```bash
git add docs/api-contract.md docs/architecture
git commit -m "docs(architecture): update Lab 5 contracts and views"
```

## Commit 3 — uso de IA, evidencia y validación final

### Declaración de IA

Actualizar `docs/AI_USAGE.md`. Incluir por cada actividad:

- herramienta usada;
- actividad realizada;
- propósito o prompt resumido;
- resultado producido;
- forma de validación humana/técnica;
- modificaciones realizadas después de la respuesta de IA.

Registrar expresamente la asistencia usada para revisar el Lab 5, corregir el dominio, implementar el cliente y preparar las instrucciones de documentación. No declarar que una prueba pasó si no se ejecutó.

### Evidencia visual obligatoria

Crear `docs/evidence/` y guardar una captura o GIF que muestre:

- un Board cargado con un identificador visible;
- por lo menos dos elementos;
- un conector visible entre ellos;
- confirmación de guardado exitoso;
- el mismo estado después de recargar.

Nombre sugerido: `docs/evidence/lab-05-save-reload.png` o `lab-05-save-reload.gif`.

Enlazar la evidencia desde el `README.md` principal y actualizar allí las instrucciones de ejecución:

```bash
mvn clean test
mvn spring-boot:run
```

Luego abrir `http://localhost:8080`.

### Validación final

Ejecutar y registrar el resultado real:

```bash
mvn clean test
```

Hacer además esta prueba manual:

1. Crear un tablero.
2. Agregar un rectángulo y un texto.
3. Mover ambos elementos.
4. Seleccionar el primero, activar conexión y seleccionar el segundo.
5. Guardar.
6. Copiar el Board ID.
7. Recargar la página.
8. Cargar el Board ID y comprobar posiciones, elementos y conector.
9. Eliminar un elemento y comprobar que también desaparece su conector.
10. Provocar un ID inválido, comprobar Error y usar Retry.

Commit sugerido:

```bash
git add README.md docs/AI_USAGE.md docs/evidence
git commit -m "docs(evidence): record Lab 5 validation and AI usage"
```

## Definición de terminado

- [ ] Existe `ADR-002-client-boundaries.md` con Context, Decision, Consequences, Trade-off y Evidence.
- [ ] El contrato REST contiene ejemplos de `CONNECTOR` y errores.
- [ ] ArchiMate incluye los cuatro módulos del cliente y las capas del backend.
- [ ] El diagrama de clases/módulos incluye frontend, backend y nuevas propiedades del dominio.
- [ ] Los archivos fuente de los diagramas y sus SVG están sincronizados.
- [ ] `AI_USAGE.md` registra herramienta, actividad, propósito, resultado, validación y cambios.
- [ ] La captura o GIF demuestra guardar y recargar un Board conectado.
- [ ] `README.md` explica cómo ejecutar y enlaza la evidencia.
- [ ] `mvn clean test` termina exitosamente.
- [ ] No se agregaron WebSockets, base de datos, framework frontend, autosave ni endpoints por gesto.
