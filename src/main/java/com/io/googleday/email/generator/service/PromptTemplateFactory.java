package com.io.googleday.email.generator.service;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PromptTemplateFactory {
    private final Map<PromptTemplateType, PromptTemplate> templates = new EnumMap<>(PromptTemplateType.class);

    public PromptTemplateFactory() {
        loadTemplates();
    }

    private void loadTemplates() {
        try {
            var resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/prompts/*.st");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null) continue;

                for (PromptTemplateType type : PromptTemplateType.values()) {
                    if (type.getFilename().equals(filename)) {
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                            String content = reader.lines().collect(Collectors.joining("\n"));
                            templates.put(type, new PromptTemplate(content));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load prompt templates", e);
        }
    }

    public PromptTemplate getTemplate(PromptTemplateType type) {
        return templates.get(type);
    }
}
