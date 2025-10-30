package dev.pablo.mcp_server.service;

import dev.pablo.mcp_server.dto.ToolDto;
import dev.pablo.mcp_server.dto.ToolResultDto;

import java.util.List;
import java.util.UUID;

public interface ToolService {
    ToolDto create(ToolDto dto);
    ToolDto update(UUID id, ToolDto dto);
    ToolDto get(UUID id);
    List<ToolDto> list();
    void delete(UUID id);
    ToolResultDto runTool(UUID id);
}
