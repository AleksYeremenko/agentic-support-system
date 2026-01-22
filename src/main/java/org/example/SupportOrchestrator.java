package org.example;

import org.example.agents.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class SupportOrchestrator {
    private final LlmClient client;
    private final KnowledgeBase kb;
    private final TicketService ticketService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ChatSessionManager sessionManager;

    public SupportOrchestrator(LlmClient client, KnowledgeBase kb, TicketService ticketService) {
        this.client = client;
        this.kb = kb;
        this.ticketService = ticketService;
        this.sessionManager = new ChatSessionManager();
    }


    public String processQuery(String userId, String query) throws Exception {
      List<Map<String, String>> userHistory = sessionManager.getHistory(userId);

        String intent = client.classifyIntent(query);
        System.out.println("[DEBUG] Intent: " + intent);

        String context = kb.findRelevantContext(query);

        Agent agent;
        String tag;

        if (intent.contains("BILLING")) {
            agent = new BillingAgent(context);
            tag = AppConfig.AGENT_B_TAG;
        }
        else if (intent.contains("TECHNICAL") || intent.contains("GENERAL")) {
            agent = new TechnicalAgent(context);
            tag = AppConfig.AGENT_A_TAG;
        }
        else {
            return AppConfig.FALLBACK_RESPONSE;
        }

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", agent.getSystemInstructions()));
        messages.addAll(userHistory);
        messages.add(Map.of("role", "user", "content", query));

        Map<String, Object> response = client.ask(messages, agent.getToolDefinitions());

        sessionManager.addToHistory(userId, "user", query);

        String finalResponseText;

        if (response.containsKey("tool_calls")) {
            List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) response.get("tool_calls");
            String rawToolOutput = executeTools(toolCalls);
            finalResponseText = client.generateNaturalResponse(query, rawToolOutput);
        } else {
            finalResponseText = (String) response.get("content");
        }

        sessionManager.addToHistory(userId, "assistant", finalResponseText);

        return tag + " " + finalResponseText;
    }

    public void logFeedback(String query, String response, boolean isPositive) {
        if (isPositive) return;

        String logEntry = String.format("[%s] NEGATIVE FEEDBACK\nQuery: %s\nResponse: %s\n----------------\n",
                java.time.LocalDateTime.now(), query, response);

        try {
            Files.writeString(Paths.get("data/feedback_log.txt"), logEntry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String executeTools(List<Map<String, Object>> toolCalls) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> call : toolCalls) {
            Map<String, Object> func = (Map<String, Object>) call.get("function");
            String name = (String) func.get("name");
            String argsJson = (String) func.get("arguments");
            Map<String, String> args = mapper.readValue(argsJson, Map.class);

            if ("get_plan_info".equals(name)) sb.append(ticketService.getPlanInfo(args.get("plan")));
            else if ("check_refund_eligibility".equals(name)) sb.append(ticketService.checkRefund(args.get("date")));
            else if ("create_ticket".equals(name)) {
                ticketService.createTicket(args.get("subject"), args.get("priority"));
                sb.append("Ticket created: ").append(args.get("subject"));
            }
        }
        return sb.toString();
    }
}