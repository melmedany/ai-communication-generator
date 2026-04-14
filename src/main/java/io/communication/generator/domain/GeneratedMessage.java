package io.communication.generator.domain;

public record GeneratedMessage(
        String subject,
        String body,
        String tone
) {
    public static final GeneratedMessage EMPTY = new GeneratedMessage("", "", "");
}
