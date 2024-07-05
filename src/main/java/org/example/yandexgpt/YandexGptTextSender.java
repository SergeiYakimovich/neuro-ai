package org.example.yandexgpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class YandexGptTextSender {
    private static final Logger log = Logger.getLogger(YandexGptTextSender.class.getName());
    private static final String TOKEN = "t1.9euelZrPypaNnpfPk5uSlJGTyp6LnO3rnpWax8eJlJrNm5vKl8zPy8qZlpvl9PcMBGZL-e83Lxen3fT3TDJjS_nvNy8Xp83n9euelZqbiomQmo-XmZSbyJyQjI2LyO_8xeuelZqbiomQmo-XmZSbyJyQjI2LyA.ljxD9WRi2dc36l9cs1f54SnwvH5oOiqOHKbLL8x7uWxzWLYPNXPlP-M28FPHDM0Cd67IRjzsFf9nGzSJtcixCA";
    private static final String ENDPOINT = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion";
    private static final String MODEL_URI = "gpt://b1g9gnpghn2con82cedb/yandexgpt-lite";

    public static void main(String[] args) {
        String result = sendTextMessageToGPT("Переведи текст","To be, or not to be: that is the question.");
        System.out.println(result);
    }

    public static String sendTextMessageToGPT(String task, String message) {
        log.info("Sending message to GPT: " + message);
        String result;

        try {
            YandexGptTextRequest yandexGptTextRequest = createTextRequest(task, message);
            var objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(yandexGptTextRequest);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + TOKEN)
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
            log.info("Received result from GPT: " + result);
        } catch (Exception e) {
            result = "Error while sending message to GPT ";
            log.info( result + e.getMessage());
        }

        return result;
    }

    private static YandexGptTextRequest createTextRequest(String task, String message) {
        return YandexGptTextRequest.builder()
                .modelUri(MODEL_URI)
                .completionOptions(YandexGptTextRequest.CompletionOptions.builder()
                        .stream(false)
                        .maxTokens(1000)
                        .temperature(0.1)
                        .build())
                .messages(List.of(
                        YandexGptTextRequest.Message.builder()
                                .role("system")
                                .text(task)
                                .build(),
                        YandexGptTextRequest.Message.builder()
                                .role("user")
                                .text(message)
                                .build()
                ))
                .build();
    }
}
