package io.communication.generator.domain;

public record MessageRequest(
        String sender,
        String receiver,
        String event,
        String reason,
        String tone
) {}
