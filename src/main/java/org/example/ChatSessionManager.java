package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatSessionManager {
    private static final String FILE_PATH = "data/history.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private Map<String, List<Map<String, String>>> sessions = new ConcurrentHashMap<>();

    public ChatSessionManager() {
        loadSessions();
    }

    public List<Map<String, String>> getHistory(String userId) {
        return sessions.computeIfAbsent(userId, k -> new ArrayList<>());
    }

    public void addToHistory(String userId, String role, String content) {
        List<Map<String, String>> history = getHistory(userId);
        history.add(Map.of("role", role, "content", content));

        while (history.size() > AppConfig.MAX_HISTORY_WINDOW) {
            history.remove(0);
        }
        saveSessions();
    }

    private void saveSessions() {
        try {
            Files.createDirectories(Paths.get("data"));
            mapper.writeValue(new File(FILE_PATH), sessions);
        } catch (IOException e) {
            System.err.println("Warning: Could not save session history.");
        }
    }

    private void loadSessions() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                sessions = mapper.readValue(file, new TypeReference<ConcurrentHashMap<String, List<Map<String, String>>>>() {});
            } catch (IOException e) {
                System.err.println("Warning: Could not load history. Starting fresh.");
            }
        }
    }
}