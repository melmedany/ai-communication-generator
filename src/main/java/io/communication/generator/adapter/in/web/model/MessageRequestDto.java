package io.communication.generator.adapter.in.web.model;

import io.communication.generator.domain.Tone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequestDto(
        @NotBlank @Size(max = 50) String sender,
        @NotBlank @Size(max = 50) String receiver,
        @NotBlank @Size(max = 100) String event,
        @NotBlank @Size(max = 300) String reason,
        @NotBlank @Size(max = 20) String tone
) {

    public Tone getTone() {
        if (tone.isBlank() || tone.equalsIgnoreCase("RANDOM")) {
             return Tone.random();
        }
        return Tone.fromString(tone.toUpperCase());
    }
}
