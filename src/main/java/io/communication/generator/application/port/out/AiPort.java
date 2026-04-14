package io.communication.generator.application.port.out;

import io.communication.generator.domain.GeneratedMessage;
import io.communication.generator.domain.MessageRequest;

public interface AiPort {
    GeneratedMessage generate(MessageRequest request);
}
