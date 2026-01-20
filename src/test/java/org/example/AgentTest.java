package org.example;

import org.example.agents.BillingAgent;
import org.example.agents.TechnicalAgent;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class AgentTest {

    @Test
    void billingAgent_ShouldHaveThreeTools() {
        BillingAgent agent = new BillingAgent();
        List<Map<String, Object>> tools = agent.getToolDefinitions();

        assertThat(tools).hasSize(3);

        assertThat(tools.toString())
                .contains("get_plan_info")
                .contains("check_refund")
                .contains("create_ticket");
    }

    @Test
    void technicalAgent_ShouldHaveContextInPrompt() {
        String context = "Server is on fire";
        TechnicalAgent agent = new TechnicalAgent(context);


        assertThat(agent.getSystemInstructions()).contains(context);

        assertThat(agent.getToolDefinitions()).isNotEmpty();
    }
}