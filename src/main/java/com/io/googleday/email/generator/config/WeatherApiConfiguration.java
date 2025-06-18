package com.io.googleday.email.generator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather-api")
public record WeatherApiConfiguration(String baseUrl, String apiKey) {
}