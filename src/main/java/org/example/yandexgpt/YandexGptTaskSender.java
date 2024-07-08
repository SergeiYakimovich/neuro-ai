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
public class YandexGptTaskSender {
    private static final String ENDPOINT = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion";
    private static final String MODEL_URI = "gpt://" + YandexGptAuth.CATALOG_ID + "/yandexgpt/latest";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
//        String result = sendTaskMessageToGPT("Переведи текст","To be, or not to be: that is the question.");
        String result = sendTaskMessageToGPT("кто написал","To be, or not to be: that is the question.");
        System.out.println(result);
    }

    public static String sendTaskMessageToGPT(String task, String message) {
        log.info("Sending task to GPT: {} - {}", task, message);
        String result;
        YandexGptAuth.getIAMTokenIfExpired();

        try {
            YandexGptTaskRequest yandexGptTaskRequest = createTaskRequest(task, message);
            String requestBody = objectMapper.writeValueAsString(yandexGptTaskRequest);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + YandexGptAuth.iamToken)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response from GPT: " + response.body());

            JsonNode nameNode = objectMapper.readTree(response.body());
            result = nameNode.get("result")
                    .get("alternatives")
                    .findValues("message").stream()
                    .map(x -> x.get("text").asText())
                    .collect(Collectors.joining(";"));
            if (result.isBlank()) {
                result = "Не удалось получить ответ";
            }
            log.info("Received result from GPT: " + result);
        } catch (Exception e) {
            result = "Error while sending message to GPT ";
            log.error( result + e.getMessage());
        }

        return result;
    }

    private static YandexGptTaskRequest createTaskRequest(String task, String message) {
        return YandexGptTaskRequest.builder()
                .modelUri(MODEL_URI)
                .completionOptions(YandexGptTaskRequest.CompletionOptions.builder()
                        .stream(false)
                        .maxTokens(1000)
                        .temperature(0.1)
                        .build())
                .messages(List.of(
                        YandexGptTaskRequest.Message.builder()
                                .role("system")
                                .text(task)
                                .build(),
                        YandexGptTaskRequest.Message.builder()
                                .role("user")
                                .text(message)
                                .build()
                ))
                .build();
    }
}
