package com.io.googleday.email.generator.service;

public enum PromptTemplateType {
    LATE("late.st"),
    MISSED("missed.st");

    private final String filename;

    PromptTemplateType(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}