MCP Server (MVP)

Pequeño MVP de servidor MCP construido con Spring Boot que expone herramientas (Tools) y permite operaciones CRUD vía HTTP, además de notificaciones por SSE.

Diseño
- Paquetes:
  - dev.pablo.mcp_server.model - entidades inmutables (Tool)
  - dev.pablo.mcp_server.dto - DTOs para entrada/salida (ToolDto, ToolResultDto)
  - dev.pablo.mcp_server.repository - repositorio en memoria (InMemoryToolRepository)
  - dev.pablo.mcp_server.service - lógica de negocio (ToolService, ToolServiceImpl)
  - dev.pablo.mcp_server.controller - endpoints REST y SSE (ToolController)
  - dev.pablo.mcp_server.exception - manejo global de excepciones

Endpoints principales
- GET  /api/tools                -> listar herramientas
- GET  /api/tools/{id}           -> obtener herramienta
- POST /api/tools                -> crear (body: ToolDto JSON)
- PUT  /api/tools/{id}           -> actualizar (body: ToolDto JSON)
- DELETE /api/tools/{id}         -> borrar
- POST /api/tools/{id}/run       -> ejecutar (simulado) una herramienta, devuelve ToolResultDto
- GET  /api/tools/stream         -> SSE stream de eventos (created, updated, deleted, run)

MCP discovery endpoints (para agentes)
- GET /mcp/tools                 -> lista metadata para que un agente descubra las tools (id, name, description, tags, runEndpoint, streamEndpoint)

Ejemplos (usando curl)

Listar:
```
curl -s http://localhost:8080/api/tools | jq
```

Crear:
```
curl -s -X POST http://localhost:8080/api/tools -H "Content-Type: application/json" -d '{"name":"My Tool","description":"Desc","tags":["analysis"]}' | jq
```

Ejecutar herramienta (simulado):
```
curl -s -X POST http://localhost:8080/api/tools/{id}/run | jq
```

Conectarse a SSE (desde terminal):
```
curl -N http://localhost:8080/api/tools/stream
```

Casos de uso realistas (ejemplo implementado)
- `Security Scanner` (tag: security): devuelve hallazgos de seguridad simulados para demostrar un caso de uso útil.
- `Dependency Analyzer` (tag: analysis): ejemplo que muestra recomendaciones sobre dependencias.
- `Code Formatter` (tag: format): muestra resultado de formateo de archivos.

Validación y calidad
- Uso de DTO y validación Jakarta Validation (name no puede estar en blanco).
- Manejo global de excepciones con respuestas JSON.
- Repositorio en memoria para PoC (fácil reemplazo por JPA u otro storage).
- SSE para notificaciones en tiempo real.

Cómo ejecutar
- Requiere Java 21 y Maven.
- Desde la raíz del proyecto:

```
./mvnw spring-boot:run
```

(O alternativamente `mvn spring-boot:run` si tienes mvn instalado).

Siguientes pasos sugeridos
- Persistencia real con Spring Data JPA + H2/Postgres.
- Autenticación/Autorización (Spring Security).
- Tests de integración que arranquen el contexto Spring y validen endpoints.
- Paginación y filtros en los listados.

Licencia: PoC para uso personal.
