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
public class YandexGptSender {
    private static final String TOKEN = "t1.9euelZqNyIqVlMrHlZ7PkoqRk4zPz-3rnpWax8eJlJrNm5vKl8zPy8qZlpvl8_cLTHBL-e8iMkgY_d3z90t6bUv57yIySBj9zef1656VmonIno2VjMeSncmKkZSemsua7_zF656VmonIno2VjMeSncmKkZSemsua.dn8CMlaqkwt1AzkZ5h0A2jlSuRSOdJ2zFYT-BcZjsLXs9PrONcnGxMkqbpfQOEZgL8jw4SXLlpu79ZzKsMRfDg";
    private static final String ENDPOINT = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion";
    private static final String MODEL_URI = "gpt://b1g9gnpghn2con82cedb/yandexgpt-lite";

    public static String sendMessage(String task,String message) {
        log.info("Sending message: {}", message);
        String result = "";

        try {
            YandexGptRequest yandexGptRequest = createRequest(task, message);
            var objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(yandexGptRequest);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + TOKEN)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            JsonNode nameNode = objectMapper.readTree(response.body());
            result = nameNode.get("result")
                    .get("alternatives")
                    .findValues("message").stream()
                    .map(x -> x.get("text").asText())
                    .collect(Collectors.joining(";"));

        } catch (Exception e) {
            log.error("Error while sending message {}", e.getMessage());
            result = "Произошла ошибка при отправке сообщения";
        }

        log.info("Received message: {}", result);
        return result;
    }

    private static YandexGptRequest createRequest(String task,String message) {
        return YandexGptRequest.builder()
                .modelUri(MODEL_URI)
                .completionOptions(YandexGptRequest.CompletionOptions.builder()
                        .stream(false)
                        .maxTokens(1000)
                        .temperature(0.1)
                        .build())
                .messages(List.of(
                        YandexGptRequest.Message.builder()
                                .role("system")
                                .text(task)
                                .build(),
                        YandexGptRequest.Message.builder()
                                .role("user")
                                .text(message)
                                .build()
                ))
                .build();
    }
}
