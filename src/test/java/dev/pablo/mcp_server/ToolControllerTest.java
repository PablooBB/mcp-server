package dev.pablo.mcp_server;

import dev.pablo.mcp_server.controller.ToolController;
import dev.pablo.mcp_server.dto.ToolDto;
import dev.pablo.mcp_server.service.ToolServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ToolControllerTest {
    @Test
    void smoke() {
        var controller = new ToolController(new ToolServiceImpl(), null);
        var list = controller.list();
        assertNotNull(list);
        assertTrue(list.size() >= 3);

        var dto = new ToolDto();
        dto.setName("Test Tool");
        dto.setDescription("Does testing");
        dto.setTags(List.of("test"));
        var response = controller.create(dto);
        assertNotNull(response);
        var created = response.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        var fetched = controller.get(created.getId());
        assertEquals("Test Tool", fetched.getName());

        dto.setDescription("Updated");
        var updated = controller.update(created.getId(), dto);
        assertEquals("Updated", updated.getDescription());

        controller.delete(created.getId());
    }
}
