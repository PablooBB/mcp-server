package dev.pablo.mcp_server.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Representa una herramienta (Tool) para el MVP.
 */
public final class Tool {
    private final UUID id;
    private final String name;
    private final String description;
    private final List<String> tags;
    private final Instant createdAt;

    public Tool(UUID id, String name, String description, List<String> tags, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags == null ? List.of() : List.copyOf(tags);
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Tool withName(String name) {
        return new Tool(this.id, name, this.description, this.tags, this.createdAt);
    }

    public Tool withDescription(String description) {
        return new Tool(this.id, this.name, description, this.tags, this.createdAt);
    }

    public Tool withTags(List<String> tags) {
        return new Tool(this.id, this.name, this.description, tags, this.createdAt);
    }
}

