package io.communication.generator.adapter.out.ai;

import io.communication.generator.domain.GeneratedMessage;
import io.communication.generator.domain.MessageRequest;
import io.communication.generator.domain.Tone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringAiAdapterTest {

    @Mock
    private ChatClient chatClient;
    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SpringAiAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SpringAiAdapter(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
    }

    @Test
    void generate_returnsMessageFromAi() {
        MessageRequest request = new MessageRequest("Mohamed", "Walaa", "Architecture meeting", "Production incident", Tone.FORMAL.name());
        GeneratedMessage expected = new GeneratedMessage(
                "Apology for Missing Architecture Meeting",
                "Dear Walaa, I apologize for missing the meeting.",
                "formal"
        );
        when(callResponseSpec.entity(GeneratedMessage.class)).thenReturn(expected);

        GeneratedMessage result = adapter.generate(request);

        assertSame(expected, result);
        verify(chatClient).prompt();
        verify(requestSpec).system(anyString());
        verify(requestSpec).user(contains("Mohamed"));
        verify(requestSpec).user(contains("Walaa"));
        verify(requestSpec).user(contains("Architecture meeting"));
        verify(requestSpec).user(contains("Production incident"));
    }

    @Test
    void generate_promptContainsAllRequestFields() {
        MessageRequest request = new MessageRequest("Alice", "Bob", "Sprint Review", "Family emergency", Tone.EMPATHETIC.name());
        GeneratedMessage expected = new GeneratedMessage("Subject", "Body", "empathetic");
        when(callResponseSpec.entity(GeneratedMessage.class)).thenReturn(expected);

        adapter.generate(request);

        verify(requestSpec).user(argThat((String prompt) ->
                prompt.contains("Alice") &&
                prompt.contains("Bob") &&
                prompt.contains("Sprint Review") &&
                prompt.contains("Family emergency") &&
                prompt.contains("empathetic")
        ));
    }

    @Test
    void generate_promptContainsTone() {
        MessageRequest request = new MessageRequest("Mohamed", "Walaa", "Meeting", "Reason", Tone.CASUAL.name());
        when(callResponseSpec.entity(GeneratedMessage.class)).thenReturn(new GeneratedMessage("S", "B", "casual"));

        adapter.generate(request);

        verify(requestSpec).user(argThat((String prompt) -> prompt.contains("casual")));
    }

    @Test
    void generate_aiReturnsNull_returnsNull() {
        MessageRequest request = new MessageRequest("Mohamed", "Walaa", "Meeting", "Reason", Tone.FORMAL.name());
        when(callResponseSpec.entity(GeneratedMessage.class)).thenReturn(null);

        GeneratedMessage result = adapter.generate(request);

        assertNull(result);
    }
}
