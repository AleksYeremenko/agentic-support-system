package org.example;

import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        try {
            printBanner();

            LlmClient client = new LlmClient();
            KnowledgeBase kb = new KnowledgeBase();
            TicketService ticketService = new TicketService();
            SupportOrchestrator orchestrator = new SupportOrchestrator(client, kb, ticketService);

            Scanner scanner = new Scanner(System.in);
            String userId = "user_default";

            System.out.println("\n[SYSTEM]: Connecting to AI Agents...");
            System.out.println("AGENT A: Welcome! I'm your Technical Assistant. My colleague from Billing is also here. How can we help you today?");

            while (true) {
                System.out.print("\nUser (Active session): ");


                ExecutorService ex = Executors.newSingleThreadExecutor();
                Future<String> result = ex.submit(scanner::nextLine);

                String userQuery;
                try {

                    userQuery = result.get(120, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    System.out.println("\n\n[SESSION EXPIRED]: No activity detected for 2 minutes. Closing chat for security.");
                    break;
                } finally {
                    ex.shutdownNow();
                }

                if (userQuery.equalsIgnoreCase("exit")) break;
                if (userQuery.trim().isEmpty()) continue;

                String aiResponse = orchestrator.processQuery(userId, userQuery);
                System.out.println("\n" + aiResponse);
            }

            System.out.println("\nThank you for using InkSupport. Goodbye!");

        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
        }
    }

    private static void printBanner() {
        System.out.println("""
    ██╗███╗   ██╗██╗  ██╗     █████╗  ██████╗ ███████╗███╗   ██╗████████╗
    ██║████╗  ██║██║ ██╔╝    ██╔══██╗██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
    ██║██╔██╗ ██║█████╔╝     ███████║██║  ███╗█████╗  ██╔██╗ ██║   ██║   
    ██║██║╚██╗██║██╔═██╗     ██╔══██║██║   ██║██╔══╝  ██║╚██╗██║   ██║   
    ██║██║ ╚████║██║  ██╗    ██║  ██║╚██████╔╝███████╗██║ ╚████║   ██║   
    ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝    ╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝   
    :: AI Agentic System ::  (v3.0 Final Release)
    """);
    }
}