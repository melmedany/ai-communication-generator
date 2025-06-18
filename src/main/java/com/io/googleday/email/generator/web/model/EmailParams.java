package com.io.googleday.email.generator.web.model;

import com.io.googleday.email.generator.service.PromptTemplateType;

import java.util.Map;

public record EmailParams(
        PromptTemplateType template,
        String tone,
        String recipientName,
        String event,
        String date,
        String arrivalTime,
        String reason,
        String senderName,
        String senderRole
) {
    public Map<String, Object> lateEmailMap() {
        return Map.of(
                "tone", tone,
                "recipientName", recipientName,
                "event", event,
                "arrivalTime", arrivalTime,
                "reason", reason,
                "senderName", senderName,
                "senderRole", senderRole,
                "city", "Utrecht"
        );
    }

    public Map<String, Object> missingEmailMap() {
        return Map.of(
                "tone", tone,
                "recipientName", recipientName,
                "event", event,
                "date", date,
                "reason", reason,
                "senderName", senderName,
                "senderRole", senderRole,
                "city", "Utrecht"
        );
    }
}
