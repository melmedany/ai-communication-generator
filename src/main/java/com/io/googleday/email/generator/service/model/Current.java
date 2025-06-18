package com.io.googleday.email.generator.service.model;

public record Current(
        double temp_c,
        Condition condition,
        double wind_kph,
        int humidity,
        double feelslike_c
) {}
