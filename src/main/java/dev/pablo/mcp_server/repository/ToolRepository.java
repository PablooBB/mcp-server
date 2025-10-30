package dev.pablo.mcp_server.repository;

import dev.pablo.mcp_server.model.Tool;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ToolRepository {
    Tool save(Tool tool);
    Optional<Tool> findById(UUID id);
    List<Tool> findAll();
    void deleteById(UUID id);
}

