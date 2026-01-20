package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.util.*;

public class LlmClient {
    private final String apiKey = AppConfig.getApiKey();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> ask(List<Map<String, String>> messages, List<Map<String, Object>> tools) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", AppConfig.MODEL_NAME);
        payload.put("temperature", AppConfig.TEMPERATURE);
        payload.put("messages", messages);

        if (tools != null && !tools.isEmpty()) {
            payload.put("tools", tools);
            payload.put("tool_choice", "auto");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var node = mapper.readTree(response.body());
        return mapper.convertValue(node.path("choices").get(0).path("message"), Map.class);
    }
}