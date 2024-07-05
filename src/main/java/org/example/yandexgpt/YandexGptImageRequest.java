package org.example.yandexgpt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class YandexGptImageRequest {
    private String mimeType;
    private List<String> languageCodes;
    private String model;
    private String content;
}
