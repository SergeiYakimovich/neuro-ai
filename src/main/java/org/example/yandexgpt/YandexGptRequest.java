package org.example.yandexgpt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class YandexGptRequest {
    private String modelUri;
    private CompletionOptions completionOptions;
    private List<Message> messages;

    @Builder
    @Data
    static class CompletionOptions {
        private boolean stream;
        private int maxTokens;
        private double temperature;
    }

    @Builder
    @Data
    static class Message {
        private String role;
        private String text;
    }
}
