package io.communication.generator.domain;

import java.security.SecureRandom;

public enum Tone {
    FORMAL, EMPATHETIC, CASUAL, ASSERTIVE, FRIENDLY;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static Tone random() {
        return values()[RANDOM.nextInt(values().length)];
    }

    public static Tone fromString(String input) {
        if (input == null || input.isBlank()) {
            return random();
        }

        try {
            return Tone.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            return random();
        }
    }
}
