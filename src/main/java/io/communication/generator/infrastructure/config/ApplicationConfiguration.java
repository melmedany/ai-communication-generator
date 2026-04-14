package io.communication.generator.infrastructure.config;

import io.communication.generator.adapter.out.weather.WeatherToolAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.stream.Stream;

@Configuration
public class ApplicationConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
    }

    @Bean
    public VectorStore vectorStore(
            EmbeddingModel embeddingModel,
            @Value("classpath:rag/communication-guidelines.md") Resource guidelines,
            @Value("classpath:rag/tone-examples.md") Resource toneExamples,
            @Value("classpath:rag/event-templates.md") Resource eventTemplates
    ) {
        VectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        TokenTextSplitter splitter = TokenTextSplitter.builder().build();
        try {
            List<Document> documents = Stream.of(guidelines, toneExamples, eventTemplates)
                    .map(TextReader::new)
                    .map(TextReader::read)
                    .flatMap(List::stream)
                    .toList();
            vectorStore.add(splitter.apply(documents));
        } catch (Exception e) {
            log.warn("Failed to initialize VectorStore with RAG documents: ", e);
        }
        return vectorStore;
    }

    @Bean
    public ToolCallbackProvider tools(WeatherToolAdapter weatherToolAdapter) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherToolAdapter)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatMemory chatMemory, VectorStore vectorStore, ToolCallbackProvider tools, ChatClient.Builder builder) {
        return builder
                .defaultToolCallbacks(tools)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build()
                )
                .build();
    }
}
