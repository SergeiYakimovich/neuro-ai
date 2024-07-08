package org.example.yandexgpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class YandexGptTranslateSender {
    private static final String ENDPOINT = "https://translate.api.cloud.yandex.net/translate/v2/translate";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        String result = sendTranslateMessageToGPT("ru",List.of("To be, or not to be", "that is the question."));
        System.out.println(result);
    }

    public static String sendTranslateMessageToGPT(String languageCode, List<String> messages) {
        log.info("Translate messages to GPT: " + messages);
        String result;
        YandexGptAuth.getIAMTokenIfExpired();

        try {
            YandexGptTranslateRequest translateRequest = createTranslateRequest(languageCode, messages);
            String requestBody = objectMapper.writeValueAsString(translateRequest);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + YandexGptAuth.iamToken)
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response from GPT: " + response.body());

            JsonNode nameNode = objectMapper.readTree(response.body());
            result = nameNode.get("translations")
                    .findValues("text").stream()
                    .map(JsonNode::asText)
                    .collect(Collectors.joining("\n"));
            if (result.isBlank()) {
                result = "Не удалось перевести текст";
            }
            log.info("Received result from GPT: " + result);
        } catch (Exception e) {
            result = "Error while sending messages to GPT ";
            log.error( result + e.getMessage());
        }

        return result;
    }

    private static YandexGptTranslateRequest createTranslateRequest(String languageCode, List<String> messages) {
        return YandexGptTranslateRequest.builder()
                .folderId(YandexGptAuth.CATALOG_ID)
                .texts(messages)
                .targetLanguageCode(languageCode)
                .build();
    }
}
