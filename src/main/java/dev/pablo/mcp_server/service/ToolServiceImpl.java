package dev.pablo.mcp_server.service;

import dev.pablo.mcp_server.dto.ToolDto;
import dev.pablo.mcp_server.dto.ToolResultDto;
import dev.pablo.mcp_server.model.Tool;
import dev.pablo.mcp_server.repository.InMemoryToolRepository;
import dev.pablo.mcp_server.repository.ToolRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ToolServiceImpl implements ToolService {
    private final ToolRepository repository;

    public ToolServiceImpl(ToolRepository repository) {
        this.repository = repository;
        seed();
    }

    // default constructor kept for tests that instantiate impl directly
    public ToolServiceImpl() {
        this.repository = new InMemoryToolRepository();
        seed();
    }

    private void seed() {
        // Añadir algunas herramientas de ejemplo
        create(makeDto("Code Formatter", "Formats code for many languages", List.of("format", "code", "style")));
        create(makeDto("Dependency Analyzer", "Analyzes project dependencies and suggests upgrades", List.of("analysis", "deps")));
        create(makeDto("Security Scanner", "Scans source code for common security issues", List.of("security", "scan")));
    }

    private ToolDto makeDto(String name, String desc, List<String> tags) {
        var dto = new ToolDto();
        dto.setName(name);
        dto.setDescription(desc);
        dto.setTags(tags);
        return dto;
    }

    @Override
    public ToolDto create(ToolDto dto) {
        var id = UUID.randomUUID();
        var now = Instant.now();
        var tool = new Tool(id, dto.getName(), dto.getDescription(), dto.getTags(), now);
        repository.save(tool);
        dto.setId(id);
        dto.setCreatedAt(now);
        return dto;
    }

    @Override
    public ToolDto update(UUID id, ToolDto dto) {
        var existing = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tool not found"));
        var updated = new Tool(id, dto.getName() == null ? existing.getName() : dto.getName(), dto.getDescription() == null ? existing.getDescription() : dto.getDescription(), dto.getTags() == null ? existing.getTags() : dto.getTags(), existing.getCreatedAt());
        repository.save(updated);
        return toDto(updated);
    }

    @Override
    public ToolDto get(UUID id) {
        var tool = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tool not found"));
        return toDto(tool);
    }

    @Override
    public List<ToolDto> list() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public ToolResultDto runTool(UUID id) {
        var tool = get(id);
        var r = new ToolResultDto();
        r.setToolId(tool.getId().toString());
        r.setToolName(tool.getName());
        r.setExecutedAt(java.time.Instant.now());
        if (tool.getTags() != null && tool.getTags().contains("security")) {
            r.setResultSummary("3 issues found");
            r.setResultDetails("Potential SQL injection in UserRepository.java:45\nUse prepared statements.\nMinor: hardcoded secrets in config");
        } else if (tool.getTags() != null && tool.getTags().contains("analysis")) {
            r.setResultSummary("Dependencies up to date");
            r.setResultDetails("All 42 dependencies have no critical updates. 5 minor updates available.");
        } else if (tool.getTags() != null && tool.getTags().contains("format")) {
            r.setResultSummary("Formatted 12 files");
            r.setResultDetails("Trimmed trailing spaces, applied indentation rules, sorted imports.");
        } else {
            r.setResultSummary("Execution completed");
            r.setResultDetails("No notable findings.");
        }
        return r;
    }

    private ToolDto toDto(Tool tool) {
        var dto = new ToolDto();
        dto.setId(tool.getId());
        dto.setName(tool.getName());
        dto.setDescription(tool.getDescription());
        dto.setTags(tool.getTags());
        dto.setCreatedAt(tool.getCreatedAt());
        return dto;
    }
}
