package org.example;

import org.example.agents.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class SupportOrchestrator {
    private final LlmClient client;
    private final KnowledgeBase kb;
    private final TicketService ticketService;
    private final ObjectMapper mapper = new ObjectMapper();

    public SupportOrchestrator(LlmClient client, KnowledgeBase kb, TicketService ticketService) {
        this.client = client;
        this.kb = kb;
        this.ticketService = ticketService;
    }

    public String processQuery(String query, List<Map<String, String>> chatHistory) throws Exception {
        List<Map<String, String>> limitedHistory = chatHistory.size() > AppConfig.MAX_HISTORY_WINDOW
                ? chatHistory.subList(chatHistory.size() - AppConfig.MAX_HISTORY_WINDOW, chatHistory.size())
                : chatHistory;

        String context = kb.findRelevantContext(query);

        boolean isBilling = query.toLowerCase().matches(".*(price|refund|bill|plan|money|pay|cost).*");
        Agent agent = isBilling ? new BillingAgent() : new TechnicalAgent(context);

        String tag = isBilling ? AppConfig.AGENT_B_TAG : AppConfig.AGENT_A_TAG;

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", agent.getSystemInstructions()));
        messages.addAll(limitedHistory);
        messages.add(Map.of("role", "user", "content", query));

        Map<String, Object> response = client.ask(messages, agent.getToolDefinitions());

        chatHistory.add(Map.of("role", "user", "content", query));

        String aiText = (String) response.get("content");
        String toolOutput = "";

        if (response.containsKey("tool_calls") && response.get("tool_calls") != null) {
            toolOutput = handleToolCalls((List<Map<String, Object>>) response.get("tool_calls"));
        }

        StringBuilder finalResponse = new StringBuilder();
        if (aiText != null && !aiText.isBlank()) {
            finalResponse.append(aiText);
        }
        if (!toolOutput.isEmpty()) {
            if (finalResponse.length() > 0) finalResponse.append("\n");
            finalResponse.append(toolOutput);
        }

        if (finalResponse.length() == 0) {
            finalResponse.append("I've processed your request. Is there anything else I can help you with?");
        }

        String resultString = finalResponse.toString();
        chatHistory.add(Map.of("role", "assistant", "content", resultString));

        return tag + " " + resultString;
    }

    private String handleToolCalls(List<Map<String, Object>> toolCalls) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> call : toolCalls) {
            Map<String, Object> func = (Map<String, Object>) call.get("function");
            String name = (String) func.get("name");
            String argumentsJson = (String) func.get("arguments");

            Map<String, String> args = mapper.readValue(argumentsJson, Map.class);

            if ("get_plan_info".equals(name)) {
                sb.append("\n[SYSTEM]: ").append(ticketService.getPlanInfo(args.get("plan")));
            }
            else if (name.contains("refund")) {
                sb.append("\n[SYSTEM]: ").append(ticketService.checkRefund(args.get("date")));
            }
            else if ("create_ticket".equals(name) || "create_support_ticket".equals(name)) {
                ticketService.createTicket(args.get("subject"), args.get("priority"));
                sb.append("\n[SYSTEM]: Support ticket created successfully. Subject: ").append(args.get("subject"));
            }
        }
        return sb.toString().trim();
    }
}