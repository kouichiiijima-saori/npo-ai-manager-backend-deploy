package com.saori.npo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NpoAiManagerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NpoAiManagerBackendApplication.class, args);
	}

}
