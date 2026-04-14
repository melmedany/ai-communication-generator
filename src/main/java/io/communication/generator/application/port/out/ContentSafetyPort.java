package io.communication.generator.application.port.out;

import io.communication.generator.domain.GeneratedMessage;
import io.communication.generator.domain.MessageRequest;

public interface ContentSafetyPort {
    void validateInput(MessageRequest request);
    void validateOutput(GeneratedMessage message);
}
