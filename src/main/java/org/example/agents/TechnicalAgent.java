package org.example.agents;

import java.util.*;

public class TechnicalAgent implements Agent {
    private final String context;

    public TechnicalAgent(String context) { this.context = context; }

    @Override
    public String getName() { return "Technical Specialist"; }

    @Override
    public String getSystemInstructions() {
        return "You are AGENT A, a Technical Specialist. Use context: [" + context + "].\n" +
                "STRICT RULES:\n" +
                "1. ALWAYS try to answer the user's question using the provided context first.\n" +
                "2. DO NOT call 'create_ticket' for greetings like 'hello', 'tech', or simple questions.\n" +
                "3. ONLY call 'create_ticket' if the user EXPLICITLY asks to 'open a ticket' or if troubleshooting steps are exhausted.\n" +
                "4. If you use a tool, do not provide a long text response, just the tool call.";
    }

    @Override
    public List<Map<String, Object>> getToolDefinitions() {
        return List.of(Map.of(
                "type", "function",
                "function", Map.of(
                        "name", "create_ticket",
                        "description", "Create a technical support ticket ONLY when explicitly requested",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "subject", Map.of("type", "string", "description", "Detailed issue summary"),
                                        "priority", Map.of("type", "string", "enum", List.of("Low", "Medium", "High"))
                                ),
                                "required", List.of("subject", "priority")
                        )
                )
        ));
    }
}