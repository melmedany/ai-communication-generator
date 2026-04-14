package io.communication.generator.adapter.out.weather;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class WeatherToolAdapter {

    @Tool(description = "Get current weather forecast for a specific city. "
            + "ONLY call this tool when the user's reason or event EXPLICITLY mentions weather, "
            + "travel delays caused by weather, commuting conditions, or outdoor events affected by weather. "
            + "Do NOT call this tool for general absences, meetings, sick leave, personal emergencies, or scheduling conflicts.")
    public String getWeatherForecast(String city) {
        return "Heavy rain and strong winds in %s today, causing significant travel disruptions.".formatted(city);
    }
}
