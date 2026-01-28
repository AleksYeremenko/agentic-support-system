package org.example;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KnowledgeBase {
    private static final Map<String, List<String>> SYNONYMS = new HashMap<>();

    static {
        SYNONYMS.put("login", List.of("auth", "authentication", "sign in", "password", "credential", "access"));
        SYNONYMS.put("bug", List.of("error", "issue", "defect", "failure", "crash", "broken"));
        SYNONYMS.put("plan", List.of("subscription", "tier", "pro", "basic", "upgrade"));
    }

    public String findRelevantContext(String query) {
        try {
            Path docsPath = Paths.get("docs");
            if (!Files.exists(docsPath)) return "";

            Set<String> searchTerms = expandQuery(query.toLowerCase());

            try (Stream<Path> paths = Files.walk(docsPath)) {
                return paths
                        .filter(Files::isRegularFile)
                        .map(path -> scoreDocument(path, searchTerms))
                        .filter(doc -> doc.score > 0)
                        .sorted(Comparator.comparingInt(ScoredDoc::getScore).reversed())
                        .limit(AppConfig.MAX_DOC_RESULTS)
                        .map(ScoredDoc::getTextSnippet)
                        .collect(Collectors.joining("\n---\n"));
            }
        } catch (Exception e) {
            return "";
        }
    }

    private Set<String> expandQuery(String query) {
        Set<String> terms = new HashSet<>();
        String[] words = query.split("\\W+");
        for (String word : words) {
            if (word.length() < AppConfig.MIN_SEARCH_WORD_LENGTH) continue;
            terms.add(word);
            SYNONYMS.forEach((key, list) -> {
                if (key.equals(word) || list.contains(word)) terms.addAll(list);
            });
        }
        return terms;
    }

    private ScoredDoc scoreDocument(Path path, Set<String> terms) {
        try {
            String content = Files.readString(path);
            int score = 0;
            for (String term : terms) {
                if (path.getFileName().toString().toLowerCase().contains(term)) score += 20;
                if (content.toLowerCase().contains(term)) score += 2;
            }
            return new ScoredDoc(path.getFileName().toString(), content, score);
        } catch (IOException e) {
            return new ScoredDoc("", "", 0);
        }
    }

    private record ScoredDoc(String fileName, String text, int score) {
        public int getScore() { return score; }
        public String getTextSnippet() { return "SOURCE: " + fileName + "\n" + text; }
    }
}