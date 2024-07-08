package org.example.yandexgpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class YandexGptImageSender {
    private static final String ENDPOINT = "https://ocr.api.cloud.yandex.net/ocr/v1/recognizeText";
    private static final String IMAGE_TYPE = "JPEG";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        String encodedImage = readImageFromFile("src/main/resources/2345.jpg");
        String result = sendImageMessageToGPT(encodedImage);
        System.out.println(result);
    }

    public static String readImageFromFile(String fileName) throws IOException {
        byte[] image = Files.readAllBytes(Path.of(fileName));
        byte[] encodedImage = Base64.encodeBase64(image);

        return new String(encodedImage, StandardCharsets.UTF_8);
    }

    public static String sendImageMessageToGPT(String fileName) {
        log.info("Sending image to GPT: " + fileName);
        String result;

        try {
            String imageContent = readImageFromFile(fileName);
            YandexGptImageRequest yandexGptImageRequest = createImageRequest(IMAGE_TYPE, imageContent);

            String requestBody = objectMapper.writeValueAsString(yandexGptImageRequest);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + YandexGptAuth.iamToken)
                    .header("Content-Type", "application/json")
                    .header("x-folder-id", YandexGptAuth.CATALOG_ID)
//                    .header("x-data-logging-enabled", "true")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response from GPT: " + response.body());

            JsonNode nameNode = objectMapper.readTree(response.body());
            result = nameNode.get("result")
                    .get("textAnnotation")
                    .get("fullText").asText()
                    .replaceAll("\n","").trim();
            if (result.isBlank()) {
                result = "Не удалось определить изображение";
            }
            log.info("Received result from GPT: " + result);
        } catch (Exception e) {
            result = "Error while sending image to GPT ";
            log.error( result + e.getMessage());
        }

        return result;
    }

    private static YandexGptImageRequest createImageRequest(String imageType, String content) {
        return YandexGptImageRequest.builder()
                .mimeType(imageType)
//                .languageCodes(List.of("*"))
//                .model("page")
                // для рукописного текста
                .languageCodes(List.of("ru", "en"))
                .model("handwritten")
                .content(content)
                .build();
    }
}
