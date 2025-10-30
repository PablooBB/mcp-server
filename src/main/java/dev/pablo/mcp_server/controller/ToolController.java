package dev.pablo.mcp_server.controller;

import dev.pablo.mcp_server.dto.ToolDto;
import dev.pablo.mcp_server.dto.ToolResultDto;
import dev.pablo.mcp_server.service.ToolService;
import dev.pablo.mcp_server.service.ToolServiceImpl;
import dev.pablo.mcp_server.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tools")
public class ToolController {
    private final ToolService service;
    private final SseService sseService;

    public ToolController(ToolService service, SseService sseService) {
        this.service = service;
        this.sseService = sseService;
    }

    /**
     * Deprecated constructor mainly for tests or quick instantiation.
     */
    @Deprecated
    public ToolController() {
        this(new ToolServiceImpl(), new SseService());
    }

    @GetMapping
    public List<ToolDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ToolDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<ToolDto> create(@Valid @RequestBody ToolDto dto) {
        var created = service.create(dto);
        notifyClients("created", created);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ToolDto update(@PathVariable UUID id, @Valid @RequestBody ToolDto dto) {
        var updated = service.update(id, dto);
        notifyClients("updated", updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        notifyClients("deleted", id.toString());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return sseService.subscribe();
    }

    @PostMapping("/{id}/run")
    public ToolResultDto runTool(@PathVariable UUID id) {
        var result = service.runTool(id);
        sseService.notifyClients("run", result);
        return result;
    }

    private void notifyClients(String event, Object data) {
        sseService.notifyClients(event, data);
    }
}
