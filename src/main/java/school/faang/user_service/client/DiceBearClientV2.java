package school.faang.user_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Slf4j
public class DiceBearClientV2 {

    @Value("${dice.bear.client.baseUrl}")
    private String baseUrl;

    public byte[] generateAvatarPng(String style) throws Exception {
        String url = String.format("%s/%s/png", baseUrl, style);
        log.debug("Generating avatar from DiceBear: {}", url);

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream()) {
                    return inputStream.readAllBytes();
                }
            } else {
                log.warn("DiceBear returned HTTP {}: {}", responseCode, connection.getResponseMessage());
                throw new RuntimeException("DiceBear API returned error: " + responseCode);
            }

        } catch (IOException e) {
            log.error("Failed to fetch avatar from DiceBear (URL: {}): {}", url, e.getMessage(), e);
            throw new RuntimeException("Avatar generation failed due to external service error", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}