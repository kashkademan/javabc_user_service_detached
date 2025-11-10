package school.faang.user_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.exception.FileException;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiceBearClient {
    private final RestTemplate restTemplate;
    private final String seed = UUID.randomUUID().toString();

    @Value("${clients.dice-bear-client.host}")
    private String host;

    @Value("${clients.dice-bear-client.api-version}")
    private String apiVersion;

    @Value("${clients.dice-bear-client.style-name}")
    private String styleName;

    @Value("${clients.dice-bear-client.format}")
    private String format;

    @Value("${clients.dice-bear-client.size}")
    private int size;

    public String generateRandomAvatarUrl() {
        return "%s%s%s%s?seed=%s&size=%d".formatted(host, apiVersion, styleName, format, seed, size);
    }

    public boolean isUrlValid(String url) {
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    public byte[] downloadAvatar() {
        String url = generateRandomAvatarUrl();
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
            return response.getBody();
        } catch (Exception e) {
            String errorMessage = "Not valid url provided to download avatar from dice bear: %s".formatted(url);
            log.error(errorMessage);
            throw new FileException(errorMessage);
        }
    }
}