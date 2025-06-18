package com.io.googleday.email.generator.web;

import com.io.googleday.email.generator.service.PromptTemplateFactory;
import com.io.googleday.email.generator.service.PromptTemplateType;
import com.io.googleday.email.generator.web.model.EmailParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailGeneratorController {
    private final ChatClient chatClient;
    private final PromptTemplateFactory promptTemplateFactory;

    public EmailGeneratorController(ChatClient chatClient, PromptTemplateFactory promptTemplateFactory) {
        this.chatClient = chatClient;
        this.promptTemplateFactory = promptTemplateFactory;
    }

    @PostMapping("/generate-email")
    public String generateEmail(@RequestBody EmailParams params) {
        ChatClient.ChatClientRequestSpec promptRequest = chatClient.prompt().system("""
        You are an assistant writing professional emails.

        If the reason for the email includes weather-related context,
        you are allowed to use the `getWeatherForecast()` tool to include factual weather details for the provided city by user.

        DO NOT USE TOOLS UNLESS REQUIRED.
        """);


        switch (params.template()) {
            case LATE -> {
                PromptTemplate promptTemplate = promptTemplateFactory.getTemplate(PromptTemplateType.LATE);
                promptRequest.user(promptTemplate.create(params.lateEmailMap()).getContents());
            }
            case MISSED -> {
                PromptTemplate promptTemplate = promptTemplateFactory.getTemplate(PromptTemplateType.MISSED);
                promptRequest.user(promptTemplate.create(params.missingEmailMap()).getContents());
            }
            default -> throw new IllegalArgumentException("Unknown prompt template: " + params.template());
        }

        return promptRequest.call().content();
    }
}
