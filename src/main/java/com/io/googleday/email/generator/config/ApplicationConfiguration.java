package com.io.googleday.email.generator.config;

import com.io.googleday.email.generator.service.WeatherToolProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
    }

    @Bean
    public ToolCallbackProvider tools(WeatherToolProvider weatherToolProvider) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherToolProvider)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatMemory chatMemory, ToolCallbackProvider tools, ChatClient.Builder builder) {
        return builder
                .defaultToolCallbacks(tools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
