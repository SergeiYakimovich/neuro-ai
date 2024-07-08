package org.example.yandexgpt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class YandexGptTranslateRequest {
    private String folderId;
    private List<String> texts;
    private String targetLanguageCode;

    public enum YandexGptLanguageCode {
        ENGLISH("en"),
        RUSSIAN("ru");

        private final String code;

        YandexGptLanguageCode(String code) {
            this.code = code;
        }
    }
}
