package org.example.agents;

import java.util.List;
import java.util.Map;

public interface Agent {
    String getName();
    String getSystemInstructions();
    List<Map<String, Object>> getToolDefinitions();
}