package com.io.googleday.email.generator.service;

import com.io.googleday.email.generator.config.WeatherApiConfiguration;
import com.io.googleday.email.generator.service.model.WeatherResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherToolProvider {
    private final RestClient restClient;
    private final WeatherApiConfiguration configuration;

    public WeatherToolProvider(RestClient.Builder restClient, WeatherApiConfiguration configuration) {
        this.restClient = restClient.baseUrl(configuration.baseUrl()).build();
        this.configuration = configuration;
    }

    @Tool(description = "Get current weather condition text for a city for today")
    public String getWeatherForecast(String city) {
        WeatherResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/current.json")
                        .queryParam("key", configuration.apiKey())
                        .queryParam("q", city)
                        .build())
                .retrieve()
                .body(WeatherResponse.class);

        if (response != null && response.current() != null && response.current().condition() != null) {
            return response.current().condition().text();
        }

        return "No weather data available";
    }
}