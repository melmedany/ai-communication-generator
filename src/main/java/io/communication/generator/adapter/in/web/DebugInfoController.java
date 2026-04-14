package io.communication.generator.adapter.in.web;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugInfoController {

    private final Environment environment;
    private final List<ChatModel> chatModels;
    private final List<EmbeddingModel> embeddingModels;

    public DebugInfoController(Environment environment,
                               List<ChatModel> chatModels,
                               List<EmbeddingModel> embeddingModels) {
        this.environment = environment;
        this.chatModels = chatModels;
        this.embeddingModels = embeddingModels;
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new LinkedHashMap<>();

        String[] activeProfiles = environment.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "default";
        info.put("activeProfile", profile);

        // Derive mode dynamically from configuration rather than hardcoding
        String chatProvider = property("spring.ai.model.chat", "unknown");
        String embeddingProvider = property("spring.ai.model.embedding", "unknown");
        info.put("chatProvider", chatProvider);
        info.put("embeddingProvider", embeddingProvider);
        info.put("mode", deriveMode(chatProvider));

        String chatModelNames = chatModels.stream()
                .map(m -> m.getClass().getSimpleName())
                .collect(Collectors.joining(", "));
        info.put("chatModels", chatModelNames.isEmpty() ? "none" : chatModelNames);

        String embeddingModelNames = embeddingModels.stream()
                .map(m -> m.getClass().getSimpleName())
                .collect(Collectors.joining(", "));
        info.put("embeddingModels", embeddingModelNames.isEmpty() ? "none" : embeddingModelNames);

        info.put("javaVersion", System.getProperty("java.version"));
        info.put("springBootVersion", org.springframework.boot.SpringBootVersion.getVersion());

        putIfPresent(info, "ollama.chat.model", property("spring.ai.ollama.chat.options.model", null));
        putIfPresent(info, "ollama.embedding.model", property("spring.ai.ollama.embedding.options.model", null));
        putIfPresent(info, "openai.baseUrl", property("spring.ai.openai.base-url", null));

        return ResponseEntity.ok(info);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new LinkedHashMap<>();

        boolean chatReady = chatModels.stream().anyMatch(Objects::nonNull);
        boolean embedReady = embeddingModels.stream().anyMatch(Objects::nonNull);

        String status;
        if (chatReady && embedReady) {
            status = "UP";
        } else if (chatReady || embedReady) {
            status = "UNHEALTHY";
        } else {
            status = "DOWN";
        }

        String[] activeProfiles = environment.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "default";

        health.put("status", status);
        health.put("activeProfile", profile);
        health.put("mode", deriveMode(property("spring.ai.model.chat", "unknown")));
        health.put("timestamp", OffsetDateTime.now().toString());

        return ResponseEntity.ok(health);
    }

    private String property(String key, String def) {
        String v = environment.getProperty(key);
        return v == null || v.isBlank() ? def : v;
    }

    private void putIfPresent(Map<String, Object> map, String key, String value) {
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
    }

    private String deriveMode(String chatProvider) {
        if (chatProvider == null) return "unknown";
        return switch (chatProvider.toLowerCase()) {
            case "ollama" -> "Local (Ollama)";
            case "openai" -> "Cloud (OpenAI-compatible)";
            default -> chatProvider;
        };
    }
}
