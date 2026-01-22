package org.example.agents;

import java.time.LocalDate;
import java.util.*;

public class BillingAgent implements Agent {
    private final String context;

    public BillingAgent(String context) { this.context = context; }
    public BillingAgent() { this.context = ""; }

    @Override
    public String getName() { return "Billing Specialist"; }

    @Override
    public String getSystemInstructions() {
        String currentDate = LocalDate.now().toString();

        return "You are AGENT B, a Billing Specialist for InkSoftware.\n" +
                "CURRENT DATE: " + currentDate + "\n" +
                "Context from Knowledge Base: [" + context + "].\n\n" +

                "GOALS:\n" +
                "1. Answer questions about prices, plans, refunds, and payments.\n" +
                "2. Calculate dates relative to CURRENT DATE (e.g., if user says '3 days ago', calculate the exact date).\n" +
                "3. Check Chat History if asked about user identity.\n\n" +

                "KNOWLEDGE STRATEGY (HYBRID RAG):\n" +
                "1. FIRST, check the [Context] provided above for specific company prices or policies. If found, use it.\n" +
                "2. SECOND, if the [Context] is missing specific details, USE YOUR GENERAL FINANCIAL KNOWLEDGE.\n" +
                "   - Example: If the user asks 'What is VAT?' or 'How do refunds work generally?' and it's not in docs, explain the concept.\n" +
                "   - Add a disclaimer: 'Note: This is general billing information, as no specific internal policy was found.'\n\n" +

                "STRICT BOUNDARIES:\n" +
                "- DO NOT answer non-billing questions (e.g., cooking, coding bugs, history).\n" +
                "- If asked about technical bugs, suggest asking the Technical Team (Agent A).\n" +
                "- Use 'check_refund_eligibility' only if a date is provided or calculated.";
    }

    @Override
    public List<Map<String, Object>> getToolDefinitions() {
        return List.of(
                Map.of("type", "function", "function", Map.of(
                        "name", "get_plan_info", "description", "Get pricing",
                        "parameters", Map.of("type", "object", "properties", Map.of("plan", Map.of("type", "string"))))),
                Map.of("type", "function", "function", Map.of(
                        "name", "check_refund_eligibility", "description", "Check refund YYYY-MM-DD",
                        "parameters", Map.of("type", "object", "properties", Map.of("date", Map.of("type", "string"))))),
                Map.of("type", "function", "function", Map.of(
                        "name", "create_ticket", "description", "Create billing ticket",
                        "parameters", Map.of("type", "object", "properties", Map.of("subject", Map.of("type", "string"), "priority", Map.of("type", "string")))))
        );
    }
}