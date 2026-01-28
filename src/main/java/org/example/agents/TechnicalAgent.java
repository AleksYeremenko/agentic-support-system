package org.example.agents;

import org.example.ToolRegistry;
import java.util.List;
import java.util.Map;

public class TechnicalAgent implements Agent {
    private final String ragContext;

    public TechnicalAgent(String ragContext) {
        this.ragContext = ragContext;
    }

    @Override
    public String getName() { return "Technical Specialist"; }

    @Override
    public String getSystemInstructions() {
        return "You are AGENT A, a Technical Specialist.\n" +
                "KNOWLEDGE BASE CONTEXT:\n" + ragContext + "\n\n" +

                "GOALS:\n" +
                "1. Solve bugs, errors, and system issues using the Context.\n" +
                "2. If the Context doesn't have the answer, use general technical knowledge but add a disclaimer.\n\n" +

                "STRICT BOUNDARIES:\n" +
                "- DO NOT answer billing questions.\n" +
                "- Only create a ticket if you cannot solve the issue immediately.";
    }

    @Override
    public List<Map<String, Object>> getToolDefinitions() {
        return List.of(ToolRegistry.CREATE_TICKET);
    }
}