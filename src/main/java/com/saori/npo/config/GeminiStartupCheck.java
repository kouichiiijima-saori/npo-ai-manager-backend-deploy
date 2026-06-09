package com.saori.npo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GeminiStartupCheck implements CommandLineRunner {

    private final GeminiProperties geminiProperties;

    public GeminiStartupCheck(GeminiProperties geminiProperties) {
        this.geminiProperties = geminiProperties;
    }

    @Override
    public void run(String... args) {
        System.out.println("Gemini model = " + geminiProperties.getModel());

        if (geminiProperties.getKey() == null || geminiProperties.getKey().isBlank()) {
            System.out.println("Gemini API Key = NOT FOUND");
        } else {
            System.out.println("Gemini API Key = FOUND");
        }
    }
}