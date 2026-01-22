package org.example;

import io.github.cdimascio.dotenv.Dotenv;

public final class AppConfig {
    private AppConfig() {}
    private static final Dotenv dotenv = Dotenv.load();

    public static String getApiKey() { return dotenv.get("LLM_API_KEY"); }

    public static final String MODEL_NAME = "llama-3.3-70b-versatile";
    public static final double TEMPERATURE = 0.0;

    public static final int MAX_HISTORY_WINDOW = 10;

    public static final int MIN_SEARCH_WORD_LENGTH = 3;
    public static final int MAX_DOC_RESULTS = 2;

    public static final int REFUND_POLICY_DAYS = 14;
    public static final int MIN_TICKET_SUBJECT_LENGTH = 5;

    public static final String AGENT_A_TAG = "AGENT A:";
    public static final String AGENT_B_TAG = "AGENT B:";

    public static final String FALLBACK_RESPONSE = "I apologize, but I am specialized only in Technical Support and Billing queries. I cannot assist with unrelated topics.";
}