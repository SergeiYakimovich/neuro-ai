package org.example.yandexgpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
public class YandexGptAuth {
    public static final String CATALOG_ID = "b1g9gnpghn2con82cedb";
    private static final String YANDEX_CODE = "eTBfQWdBQUFBQm1SUmFDQUFUdXdRQUFBQUVKSy1QTkFBQ1h4VFkyR0pOTmVZWVYzOEE1eGRBcEFia1l5UQ==";
    private static final String AUTH_URL = "https://iam.api.cloud.yandex.net/iam/v1/tokens";
    public static String iamToken = "";
    private static LocalDateTime iamTokenRefreshDate = LocalDateTime.now();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {
        getIAMTokenIfExpired();
        System.out.println(iamToken);
        System.out.println(iamTokenRefreshDate);

    }

    private static String decodeCode(String code) {
        return new String(Base64.decodeBase64(code), java.nio.charset.StandardCharsets.UTF_8);
    }

    public static void getIAMTokenIfExpired() {
        if (LocalDateTime.now().isAfter(iamTokenRefreshDate)) {
            log.info("Token is expired");
            getIAMToken();
        }
    }

    public static void getIAMToken() {
        log.info("Getting new token");
        Map<String, String> yandexRequest = Map.of("yandexPassportOauthToken", decodeCode(YANDEX_CODE));
        try {
            String requestBody = objectMapper.writeValueAsString(yandexRequest);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AUTH_URL))
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode nameNode = objectMapper.readTree(response.body());
            iamToken = nameNode.get("iamToken").asText();
            String expiresAt = nameNode.get("expiresAt").asText();
            iamTokenRefreshDate = LocalDateTime.parse(expiresAt, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.info("Error while getting token " + e.getMessage());
        }
    }

}
