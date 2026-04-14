package io.communication.generator.application.service;

import io.communication.generator.application.port.in.GenerateCommunicationUseCase;
import io.communication.generator.application.port.out.AiPort;
import io.communication.generator.application.port.out.ContentSafetyPort;
import io.communication.generator.domain.GeneratedMessage;
import io.communication.generator.domain.MessageRequest;
import org.springframework.stereotype.Service;

@Service
public class CommunicationService implements GenerateCommunicationUseCase {

    private final AiPort aiPort;
    private final ContentSafetyPort contentSafetyPort;

    public CommunicationService(AiPort aiPort, ContentSafetyPort contentSafetyPort) {
        this.aiPort = aiPort;
        this.contentSafetyPort = contentSafetyPort;
    }

    @Override
    public GeneratedMessage generate(MessageRequest request) {
        contentSafetyPort.validateInput(request);
        GeneratedMessage message = aiPort.generate(request);
        contentSafetyPort.validateOutput(message);
        return message;
    }
}
