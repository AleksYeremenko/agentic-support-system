package org.example.agents;

import java.util.*;

public class BillingAgent implements Agent {
    @Override
    public String getName() { return "Billing Specialist"; }

    @Override
    public String getSystemInstructions() {
        return "You are AGENT B, a Billing Specialist.\n" +
                "STRICT RULES:\n" +
                "1. Use 'get_plan_info' only if the user asks about prices or plans.\n" +
                "2. Use 'check_refund_eligibility' only if a specific date is provided for a refund.\n" +
                "3. DO NOT create a ticket unless the user explicitly asks for it.\n" +
                "4. For greetings, just introduce yourself and ask how you can help with billing.";
    }

    @Override
    public List<Map<String, Object>> getToolDefinitions() {
        return List.of(
                Map.of("type", "function", "function", Map.of(
                        "name", "get_plan_info",
                        "description", "Get plan pricing. Use only when asked about costs.",
                        "parameters", Map.of("type", "object", "properties", Map.of("plan", Map.of("type", "string")))
                )),
                Map.of("type", "function", "function", Map.of(
                        "name", "check_refund_eligibility",
                        "description", "Check refund dates. Use only when a date is provided.",
                        "parameters", Map.of("type", "object", "properties", Map.of("date", Map.of("type", "string")))
                )),
                Map.of("type", "function", "function", Map.of(
                        "name", "create_ticket",
                        "description", "Create a billing ticket ONLY on explicit user request.",
                        "parameters", Map.of("type", "object", "properties", Map.of(
                                "subject", Map.of("type", "string"),
                                "priority", Map.of("type", "string")
                        ))
                ))
        );
    }
}