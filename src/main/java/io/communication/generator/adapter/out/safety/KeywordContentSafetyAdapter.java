package io.communication.generator.adapter.out.safety;

import io.communication.generator.application.port.out.ContentSafetyPort;
import io.communication.generator.domain.GeneratedMessage;
import io.communication.generator.domain.MessageRequest;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class KeywordContentSafetyAdapter implements ContentSafetyPort {

    private static final Pattern SENSITIVE_DATA_PATTERN = Pattern.compile(
            "(?i)(send|share|provide|give|enter|type|submit|include).{0,30}(your|my|the).{0,20}(password|ssn|social security|credit card|card number|cvv|pin|secret key|api[- ]?key|access[- ]?token)"
                    + "|(?i)\\b\\d{3}[- ]?\\d{2}[- ]?\\d{4}\\b"
                    + "|(?i)\\b\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}\\b"
    );

    private static final Pattern INJECTION_PATTERN = Pattern.compile(
            "(?i)(ignore|disregard|forget|override).{0,30}(all|every|previous|prior|above).{0,20}(instruction|prompt|system|rule|guideline)"
                    + "|(?i)(you are now|from now on|new role|new instruction|switch to|enter .{0,15}mode)"
                    + "|(?i)(act as|pretend to be|roleplay as|simulate being|behave as).{0,30}(unrestricted|unfiltered|jailbroken|evil|DAN)"
                    + "|(?i)(output|reveal|show|print|display|leak|dump|repeat).{0,30}(system|initial|original|hidden|internal).{0,20}(prompt|instruction|message|context)"
    );

    @Override
    public void validateInput(MessageRequest request) {
        checkInjection(request.sender());
        checkInjection(request.receiver());
        checkInjection(request.event());
        checkInjection(request.reason());
    }

    @Override
    public void validateOutput(GeneratedMessage message) {
        if (SENSITIVE_DATA_PATTERN.matcher(message.body()).find()) {
            throw new IllegalArgumentException("Generated message contains forbidden content");
        }
    }

    private void checkInjection(String input) {
        if (INJECTION_PATTERN.matcher(input).find()) {
            throw new IllegalArgumentException("Input contains disallowed patterns");
        }
    }
}
