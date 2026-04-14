package io.communication.generator.application.port.in;

import io.communication.generator.domain.GeneratedMessage;
import io.communication.generator.domain.MessageRequest;

public interface GenerateCommunicationUseCase {
    GeneratedMessage generate(MessageRequest request);
}
