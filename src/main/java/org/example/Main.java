package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
        System.setProperty("org.slf4j.simpleLogger.showLogName", "false");

        try {
            LlmClient client = new LlmClient();
            KnowledgeBase kb = new KnowledgeBase();
            TicketService ticketService = new TicketService();

            SupportOrchestrator orchestrator = new SupportOrchestrator(client, kb, ticketService);
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== AI Support System Online ===");
            System.out.println("Ready to assist with Tech and Billing. Type 'exit' to quit.");

            while (true) {
                System.out.print("\nUser: ");
                String userQuery = scanner.nextLine();
                if (userQuery.equalsIgnoreCase("exit")) break;

                String aiResponse = orchestrator.processQuery(userQuery);

                System.out.println("\nAI Response:\n" + aiResponse);
            }
        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
        }
    }
}