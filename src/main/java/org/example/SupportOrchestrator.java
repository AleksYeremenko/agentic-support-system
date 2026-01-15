package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.regex.*;

public class SupportOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(SupportOrchestrator.class);
    private final LlmClient client;
    private final KnowledgeBase kb;
    private final TicketService tickets;
    private final List<Map<String, String>> history = new ArrayList<>();

    private static final Pattern TOOL_PATTERN = Pattern.compile("\\[([A-Z_]+):\\s*([^\\]]+)\\]");

    public SupportOrchestrator(LlmClient client, KnowledgeBase kb, TicketService tickets) {
        this.client = client;
        this.kb = kb;
        this.tickets = tickets;
    }

    public String processQuery(String userQuery) throws Exception {
        String context = kb.findContext(userQuery);

        String systemPrompt = "You are a professional support system. Route to " + AppConfig.AGENT_A_TAG + " or " + AppConfig.AGENT_B_TAG + ".\n\n" +
                AppConfig.AGENT_A_TAG + " (Tech): Use context [" + (context.isEmpty() ? "No docs" : context) + "].\n" +
                "If info is missing, say you don't know. Greetings (Hi/Hello) are allowed.\n\n" +
                AppConfig.AGENT_B_TAG + " (Billing): Tools: [GET_PLAN: Name], [CHECK_REFUND: YYYY-MM-DD], [CREATE_TICKET: Subj, Priority].\n\n" +
                "Start response with the agent tag. Be professional.";

        history.add(Map.of("role", "user", "content", userQuery));

        while (history.size() > AppConfig.MAX_HISTORY_WINDOW) {
            history.remove(0);
        }

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.addAll(history);

        String rawResponse = client.ask(messages);
        handleTools(rawResponse);

        String finalResponse = sanitizeResponse(rawResponse);
        history.add(Map.of("role", "assistant", "content", finalResponse));
        return finalResponse;
    }

    private void handleTools(String text) {
        Matcher matcher = TOOL_PATTERN.matcher(text);
        while (matcher.find()) {
            String command = matcher.group(1);
            String[] args = matcher.group(2).split(",");
            try {
                switch (command) {
                    case "GET_PLAN" -> tickets.getPlanInfo(args[0]);
                    case "CHECK_REFUND" -> tickets.checkRefund(args[0]);
                    case "CREATE_TICKET" -> tickets.createTicket(args[0], args.length > 1 ? args[1].trim() : "Medium");
                    default -> logger.warn("Unknown tool called: {}", command);
                }
            } catch (Exception e) {
                logger.error("Tool execution failed for: {}", command, e);
            }
        }
    }

    private String sanitizeResponse(String response) {
        return response.replaceAll("\\[[A-Z_]+:.*?\\]", "").trim();
    }
}