package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
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

        String jsonBody = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var node = mapper.readTree(response.body());

        if (node.has("error")) {
            throw new RuntimeException("LLM API Error: " + node.get("error").toString());
        }

        return mapper.convertValue(node.path("choices").get(0).path("message"), Map.class);
    }

    public String classifyIntent(String query) {
        try {
            String prompt = """
                Classify the user query into exactly one category:
                
                - TECHNICAL (bugs, errors, login, system issues, OR ANY MENTION OF 'Agent A')
                - BILLING (price, cost, refund, plan, payment, dates relative to purchase, OR ANY MENTION OF 'Agent B')
                - GENERAL (greetings, chitchat, questions about identity, memory, 'what is my name', 'hello')
                - UNKNOWN (weather, cooking, nonsense, completely unrelated topics)
                
                Query: "%s"
                
                Return ONLY the category name.
                """.formatted(query);

            List<Map<String, String>> messages = List.of(Map.of("role", "user", "content", prompt));

            Map<String, Object> response = ask(messages, null);
            String content = (String) response.get("content");
            return content != null ? content.trim().toUpperCase() : "UNKNOWN";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    public String generateNaturalResponse(String userQuery, String toolOutput) {
        try {
            String today = LocalDate.now().toString();
            String prompt = """
                You are a helpful support assistant.
                Current Date: %s
                User Question: "%s"
                
                SYSTEM DATA (Tool Execution Result):
                %s
                
                TASK:
                Write a natural, helpful response to the user based STRICTLY on the System Data above.
                - If the data says "Ticket created", confirm it politely with the ticket subject.
                - If the data mentions a refund status, explain it clearly.
                - Do NOT mention "JSON", "null", or internal code execution.
                """.formatted(today, userQuery, toolOutput);

            List<Map<String, String>> messages = List.of(
                    Map.of("role", "system", "content", "You are a helpful assistant."),
                    Map.of("role", "user", "content", prompt)
            );


            Map<String, Object> response = ask(messages, null);
            return (String) response.get("content");
        } catch (Exception e) {
            return "Action completed. Details: " + toolOutput;
        }
    }
}