package org.example.agents;

import java.util.*;

public class TechnicalAgent implements Agent {
    private final String context;

    public TechnicalAgent(String context) { this.context = context; }
    public TechnicalAgent() { this.context = ""; }

    @Override
    public String getName() { return "Technical Specialist"; }

    @Override
    public String getSystemInstructions() {
        return "You are AGENT A, a Technical Specialist for InkSoftware.\n" +
                "Context from Knowledge Base: [" + context + "].\n\n" +

                "GOALS:\n" +
                "1. Answer technical support questions (bugs, errors, configuration, API, login).\n" +
                "2. Engage in professional small talk (greetings).\n" +
                "3. Check Chat History if asked about user identity.\n\n" +

                "KNOWLEDGE STRATEGY (HYBRID RAG):\n" +
                "1. FIRST, check the [Context] provided above. If the answer is there, use it and cite the source.\n" +
                "2. SECOND, if the [Context] is empty or does not cover the specific error, USE YOUR GENERAL TECHNICAL KNOWLEDGE.\n" +
                "   - Example: If the user asks about 'HTTP 504' and it's not in docs, explain what a Gateway Timeout is generally.\n" +
                "   - However, if you use general knowledge, add a small disclaimer: 'Note: This is general technical advice, as no specific internal documentation was found for this issue.'\n\n" +

                "STRICT BOUNDARIES:\n" +
                "- EVEN if you don't find it in docs, DO NOT answer non-technical questions (e.g., cooking, movies, life advice).\n" +
                "- If the user asks 'How to cook fish?', refuse politely, stating you only handle Technical & Billing queries.\n" +
                "- DO NOT call 'create_ticket' for simple questions.";
    }

    @Override
    public List<Map<String, Object>> getToolDefinitions() {
        return List.of(Map.of("type", "function", "function", Map.of(
                "name", "create_ticket",
                "description", "Create ticket",
                "parameters", Map.of("type", "object", "properties", Map.of(
                        "subject", Map.of("type", "string"),
                        "priority", Map.of("type", "string", "enum", List.of("Low", "Medium", "High"))
                ), "required", List.of("subject", "priority"))
        )));
    }
}