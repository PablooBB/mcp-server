package dev.pablo.mcp_server.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        var emitter = new SseEmitter(Duration.ofHours(1).toMillis());
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        try {
            emitter.send(SseEmitter.event().name("init").data("connected"));
        } catch (IOException e) {
            // ignore
        }
        return emitter;
    }

    public void notifyClients(String event, Object data) {
        var dead = new CopyOnWriteArrayList<SseEmitter>();
        for (var emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(event).data(data));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        emitters.removeAll(dead);
    }
}

