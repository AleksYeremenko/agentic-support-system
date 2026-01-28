package org.example;

import org.example.agents.BillingAgent;
import org.example.agents.TechnicalAgent;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AgentTest {

    @Test
    void testBillingAgentTools() {

        BillingAgent agent = new BillingAgent("test_user_123");

        List<Map<String, Object>> tools = agent.getToolDefinitions();

        boolean hasRefundTool = tools.stream()
                .anyMatch(t -> {
                    Map<String, Object> func = (Map<String, Object>) t.get("function");
                    return "check_refund_eligibility".equals(func.get("name"));
                });

        assertTrue(hasRefundTool, "Billing Agent must have refund tool");
    }

    @Test
    void testTechnicalAgentTools() {

        TechnicalAgent agent = new TechnicalAgent("System is down error 500");

        List<Map<String, Object>> tools = agent.getToolDefinitions();

        boolean hasTicketTool = tools.stream()
                .anyMatch(t -> {
                    Map<String, Object> func = (Map<String, Object>) t.get("function");
                    return "create_ticket".equals(func.get("name"));
                });

        assertTrue(hasTicketTool, "Technical Agent must have ticket tool");
    }
}