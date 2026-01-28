package org.example.agents;

import org.example.ToolRegistry;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class BillingAgent implements Agent {
    private final String userId;

    public BillingAgent(String userId) {
        this.userId = userId;
    }

    @Override
    public String getName() { return "Billing Specialist"; }

    @Override
    public String getSystemInstructions() {
        String currentDate = LocalDate.now().toString();
        return "You are AGENT B, a Billing Specialist.\n" +
                "CURRENT DATE: " + currentDate + "\n" +
                "User ID: " + userId + "\n\n" +
                "GOALS:\n" +
                "1. Answer questions about prices, plans, refunds.\n" +
                "2. Calculate dates relative to CURRENT DATE.\n" +
                "STRICT BOUNDARIES:\n" +
                "- DO NOT answer technical questions.\n" +
                "- Use 'check_refund_eligibility' only if a date is provided.";
    }

    @Override
    public List<Map<String, Object>> getToolDefinitions() {
        return List.of(ToolRegistry.GET_PLAN_INFO, ToolRegistry.CHECK_REFUND);
    }
}