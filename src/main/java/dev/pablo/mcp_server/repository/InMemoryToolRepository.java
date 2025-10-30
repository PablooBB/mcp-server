package dev.pablo.mcp_server.repository;

import dev.pablo.mcp_server.model.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryToolRepository implements ToolRepository {
    private final Map<UUID, Tool> store = new ConcurrentHashMap<>();

    @Override
    public Tool save(Tool tool) {
        store.put(tool.getId(), tool);
        return tool;
    }

    @Override
    public Optional<Tool> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Tool> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }
}
