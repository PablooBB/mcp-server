package dev.pablo.mcp_server.controller;

import dev.pablo.mcp_server.service.ToolService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mcp")
public class McpController {
    private final ToolService service;

    public McpController(ToolService service) {
        this.service = service;
    }

    /**
     * Endpoint para que un agente MCP descubra las tools disponibles.
     * Devuelve una lista de metadatos mínimas por tool.
     */
    @GetMapping("/tools")
    public List<Map<String, Object>> listTools() {
        return service.list().stream().map(t -> Map.of(
                "id", t.getId().toString(),
                "name", t.getName(),
                "description", t.getDescription(),
                "tags", t.getTags(),
                "runEndpoint", "/api/tools/" + t.getId() + "/run",
                "streamEndpoint", "/api/tools/stream"
        )).collect(Collectors.toList());
    }
}
