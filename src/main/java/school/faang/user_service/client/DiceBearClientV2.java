package school.faang.user_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiceBearClientV2 {

    private final RestTemplate restTemplate;

    @Value("${dice.bear.client.baseUrl}")
    private String baseUrl;

    @Value("${avatar.default.template}")
    private String defaultAvatarTemplate;

    private static final Random random = new Random();

    public byte[] generateAvatarPng(String style) {
        String seed = UUID.randomUUID().toString().substring(0, 8);
        String url = String.format("%s/%s/png?seed=%s", baseUrl, style, seed);
        log.debug("Requesting avatar from DiceBear: {}", url);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully received avatar ({} bytes)", response.getBody().length);
                return response.getBody();
            } else {
                log.warn("DiceBear returned non-OK response: {}", response.getStatusCode());
                return generateDefaultAvatarBytes();
            }

        } catch (RestClientException e) {
            log.error("Failed to fetch avatar from DiceBear: {}", e.getMessage(), e);
            return generateDefaultAvatarBytes();
        }
    }

    private byte[] generateDefaultAvatarBytes() {
        String color = generateRandomColor();
        String svg = defaultAvatarTemplate.formatted(color);
        log.info("Generated default avatar with color #{}", color);
        return svg.getBytes(StandardCharsets.UTF_8);
    }

    private String generateRandomColor() {
        return String.format("%06x", random.nextInt(0xFFFFFF));
    }
}