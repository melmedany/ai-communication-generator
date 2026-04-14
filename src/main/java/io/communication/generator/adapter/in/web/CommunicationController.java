package io.communication.generator.adapter.in.web;

import io.communication.generator.adapter.in.web.model.MessageRequestDto;
import io.communication.generator.application.port.in.GenerateCommunicationUseCase;
import io.communication.generator.domain.GeneratedMessage;
import io.communication.generator.domain.MessageRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/communications")
public class CommunicationController {

    private final GenerateCommunicationUseCase useCase;

    public CommunicationController(GenerateCommunicationUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/generate")
    public ResponseEntity<GeneratedMessage> generate(@RequestBody @Valid MessageRequestDto dto) {
        String tone = dto.tone() != null ? dto.tone() : "RANDOM";
        MessageRequest request = new MessageRequest(dto.sender(), dto.receiver(), dto.event(), dto.reason(), tone);
        GeneratedMessage message = useCase.generate(request);
        return ResponseEntity.ok(message);
    }
}
