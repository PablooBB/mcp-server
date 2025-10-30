package dev.pablo.mcp_server.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

public class ToolDto {
    private UUID id;

    @NotBlank(message = "name must not be blank")
    private String name;
    private String description;
    private List<String> tags;
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
