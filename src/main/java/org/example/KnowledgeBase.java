package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class KnowledgeBase {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBase.class);
    private final Path docsPath = Paths.get("docs");

    public String findContext(String query) {
        if (query == null || query.isBlank()) return "";
        if (!Files.exists(docsPath)) {
            logger.warn("Documentation directory not found: {}", docsPath.toAbsolutePath());
            return "";
        }

        try (Stream<Path> paths = Files.walk(docsPath)) {
            return paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .map(this::readFileSafe)
                    .filter(Objects::nonNull)
                    .map(content -> new DocMatch(content, calculateScore(query, content)))
                    .filter(m -> m.score > 0)
                    .sorted(Comparator.comparingInt(DocMatch::score).reversed())
                    .limit(AppConfig.MAX_DOC_RESULTS) // Используем константу
                    .map(DocMatch::content)
                    .collect(Collectors.joining("\n---\n"));
        } catch (Exception e) {
            throw new AppException("Error while traversing knowledge base", e);
        }
    }

    private String readFileSafe(Path p) {
        try {
            return Files.readString(p);
        } catch (Exception e) {
            logger.error("Could not read file: {}", p.getFileName());
            return null;
        }
    }

    private int calculateScore(String q, String c) {
        int score = 0;
        String lowerContent = c.toLowerCase();
        String[] words = q.toLowerCase().split("\\W+");

        for (String word : words) {

            if (word.length() >= AppConfig.MIN_SEARCH_WORD_LENGTH && lowerContent.contains(word)) {
                score++;
            }
        }
        return score;
    }

    private record DocMatch(String content, int score) {}
}