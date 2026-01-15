package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import java.net.URI;
import java.net.http.*;
import java.util.*;

public class LlmClient {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL_NAME = "llama-3.3-70b-versatile";
    private static final double TEMPERATURE = 0.2;

    private final String apiKey;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public LlmClient() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("LLM_API_KEY");


        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new AppException("API key 'LLM_API_KEY' is missing in .env file");
        }
    }

    public String ask(List<Map<String, String>> messages) {
        try {
            Map<String, Object> body = Map.of(
                    "model", MODEL_NAME,
                    "messages", messages,
                    "temperature", TEMPERATURE
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                throw new AppException("LLM API request failed with status: " + resp.statusCode());
            }

            return mapper.readTree(resp.body()).path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {

            throw new AppException("Failed to communicate with AI Service", e);
        }
    }
}