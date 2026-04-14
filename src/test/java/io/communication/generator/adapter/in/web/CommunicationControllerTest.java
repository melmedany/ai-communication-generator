package io.communication.generator.adapter.in.web;

import io.communication.generator.adapter.in.web.model.MessageRequestDto;
import io.communication.generator.application.port.in.GenerateCommunicationUseCase;
import io.communication.generator.domain.GeneratedMessage;
import io.communication.generator.domain.MessageRequest;
import io.communication.generator.domain.Tone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunicationControllerTest {

    @Mock
    private GenerateCommunicationUseCase useCase;

    @InjectMocks
    private CommunicationController controller;

    @Test
    void generate_mapsRequestAndReturns200() {
        MessageRequestDto dto = new MessageRequestDto("Mohamed", "Walaa", "Architecture meeting", "Production incident", Tone.FORMAL.name());
        GeneratedMessage expected = new GeneratedMessage("Apology", "Dear Walaa...", "formal");

        when(useCase.generate(any(MessageRequest.class))).thenReturn(expected);

        ResponseEntity<GeneratedMessage> response = controller.generate(dto);

        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
        verify(useCase).generate(new MessageRequest("Mohamed", "Walaa", "Architecture meeting", "Production incident", Tone.FORMAL.name()));
    }

    @Test
    void generate_defaultsToneToRandom() {
        MessageRequestDto dto = new MessageRequestDto("Alice", "Bob", "Sprint Review", "Family emergency", null);
        GeneratedMessage expected = new GeneratedMessage("Subject", "Body", "empathetic");

        when(useCase.generate(any(MessageRequest.class))).thenReturn(expected);

        ResponseEntity<GeneratedMessage> response = controller.generate(dto);

        assertEquals(200, response.getStatusCode().value());
        verify(useCase).generate(new MessageRequest("Alice", "Bob", "Sprint Review", "Family emergency", "RANDOM"));
    }

    @Test
    void generate_propagatesExceptionFromUseCase() {
        MessageRequestDto dto = new MessageRequestDto("Mohamed", "Walaa", "Meeting", "Reason", Tone.FORMAL.name());

        when(useCase.generate(any(MessageRequest.class)))
                .thenThrow(new IllegalArgumentException("Generated message contains forbidden content"));

        assertThrows(IllegalArgumentException.class, () -> controller.generate(dto));
    }

    @Test
    void generate_useCaseReturnsNull_returns200WithNullBody() {
        MessageRequestDto dto = new MessageRequestDto("Mohamed", "Walaa", "Meeting", "Reason", Tone.CASUAL.name());

        when(useCase.generate(any(MessageRequest.class))).thenReturn(null);

        ResponseEntity<GeneratedMessage> response = controller.generate(dto);

        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
