package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    public String getPlanInfo(String plan) {
        return (plan != null && plan.toLowerCase().contains("pro")) ? "Pro Plan: $50/mo" : "Basic Plan: $20/mo";
    }

    public String checkRefund(String dateStr) {
        try {
            LocalDate purchaseDate = LocalDate.parse(dateStr.trim());
            long days = ChronoUnit.DAYS.between(purchaseDate, LocalDate.now());

            boolean isEligible = days <= AppConfig.REFUND_POLICY_DAYS;

            return isEligible ? "Eligible for refund." : "Not eligible (over " + AppConfig.REFUND_POLICY_DAYS + " days).";
        } catch (Exception e) {
            return "Error: Use YYYY-MM-DD format.";
        }
    }

    public void createTicket(String subject, String priority) {

        if (subject == null || subject.isBlank() || subject.length() < AppConfig.MIN_TICKET_SUBJECT_LENGTH) {
            logger.warn("Security/Validation Alert: Rejected ticket subject '{}'", subject);
            return;
        }

        List<String> allowedPriorities = List.of("Low", "Medium", "High");
        String finalPriority = (priority != null && allowedPriorities.contains(priority)) ? priority : "Medium";

        logger.info("Validated ticket creation: {} | {}", subject, finalPriority);
        System.out.println("\n[SYSTEM EVENT]: New Ticket Created -> " + subject + " (" + finalPriority + ")");
    }
}