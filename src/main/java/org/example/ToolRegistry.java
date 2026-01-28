package org.example;

import java.util.List;
import java.util.Map;

public class ToolRegistry {

    public static final Map<String, Object> GET_PLAN_INFO = Map.of(
            "type", "function", "function", Map.of(
                    "name", "get_plan_info", "description", "Get pricing information",
                    "parameters", Map.of("type", "object", "properties", Map.of("plan", Map.of("type", "string")))
            ));

    public static final Map<String, Object> CHECK_REFUND = Map.of(
            "type", "function", "function", Map.of(
                    "name", "check_refund_eligibility", "description", "Check refund eligibility based on purchase date (YYYY-MM-DD)",
                    "parameters", Map.of("type", "object", "properties", Map.of("date", Map.of("type", "string")), "required", List.of("date"))
            ));

    public static final Map<String, Object> CREATE_TICKET = Map.of(
            "type", "function", "function", Map.of(
                    "name", "create_ticket", "description", "Create a support ticket",
                    "parameters", Map.of("type", "object", "properties", Map.of(
                            "subject", Map.of("type", "string"),
                            "priority", Map.of("type", "string", "enum", List.of("Low", "Medium", "High"))
                    ), "required", List.of("subject", "priority"))
            ));
}