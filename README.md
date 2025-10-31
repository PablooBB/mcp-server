# MCP Server

## Descripción
Este proyecto es un servidor MCP (Model Context Protocol) basado en Spring Boot. Proporciona una API para registrar y ejecutar herramientas de manera dinámica.

## Configuración
1. Asegúrate de tener Java 17 instalado.
2. Ejecuta el servidor con el comando:
   ```bash
   mvn spring-boot:run
   ```

## Endpoints principales
- `/mcp/tools`: Lista todas las herramientas disponibles.
- `/api/tools/{id}/run`: Ejecuta una herramienta específica.
- `/api/tools/stream`: Endpoint para actualizaciones en tiempo real.

## Contribuir
1. Crea un fork del repositorio.
2. Crea una nueva rama para tus cambios.
3. Envía un pull request con tus contribuciones.

## Licencia
Este proyecto está bajo la Licencia MIT.
