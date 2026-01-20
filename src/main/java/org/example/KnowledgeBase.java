package org.example;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class KnowledgeBase {
    public String findRelevantContext(String query) {
        try {
            Path path = Paths.get("docs");
            if (!Files.exists(path)) return "";

            String[] terms = query.toLowerCase().split("\\W+");

            return Files.walk(path)
                    .filter(Files::isRegularFile)
                    .map(p -> {
                        try {
                            String content = Files.readString(p);
                            long score = 0;
                            for (String term : terms) {
                                if (term.length() < AppConfig.MIN_SEARCH_WORD_LENGTH) continue;
                                score += (content.toLowerCase().split(term, -1).length - 1);
                                if (p.getFileName().toString().toLowerCase().contains(term)) score += 10;
                            }
                            return new ScoredDoc(content, score);
                        } catch (Exception e) { return null; }
                    })
                    .filter(d -> d != null && d.score > 0)
                    .sorted(Comparator.comparingLong(d -> -d.score))
                    .limit(AppConfig.MAX_DOC_RESULTS)
                    .map(d -> d.text)
                    .collect(Collectors.joining("\n---\n"));

        } catch (Exception e) { return ""; }
    }
    private record ScoredDoc(String text, long score) {}
}