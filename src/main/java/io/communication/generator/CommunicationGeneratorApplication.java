package io.communication.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class CommunicationGeneratorApplication {
	static void main(String[] args) {
		SpringApplication.run(CommunicationGeneratorApplication.class, args);
	}

}
