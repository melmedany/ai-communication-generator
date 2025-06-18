package com.io.googleday.email.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class EmailGeneratorApplication {
	public static void main(String[] args) {
		SpringApplication.run(EmailGeneratorApplication.class, args);
	}

}
