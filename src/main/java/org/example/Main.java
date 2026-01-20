package org.example;

import java.util.*;

public class Main {
    private static final List<Map<String, String>> chatHistory = new ArrayList<>();

    public static void main(String[] args) {
        try {
            LlmClient client = new LlmClient();
            KnowledgeBase kb = new KnowledgeBase();
            TicketService ticketService = new TicketService();

            SupportOrchestrator orchestrator = new SupportOrchestrator(client, kb, ticketService);
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== AI Support System Online ===");
            System.out.println("Ready to assist. (Technical / Billing)");

            while (true) {
                System.out.print("\nUser: ");
                String userQuery = scanner.nextLine();
                if (userQuery.equalsIgnoreCase("exit")) break;

                String aiResponse = orchestrator.processQuery(userQuery, chatHistory);

                System.out.println("\nAI Response:\n" + aiResponse);
            }
        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}