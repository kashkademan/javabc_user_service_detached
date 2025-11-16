package school.faang.user_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.properties.DiceBearProperties;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiceBearClient {

    private final RestTemplate restTemplate;
    private final String seed = UUID.randomUUID().toString();
    private final DiceBearProperties diceBearProperties;

    public String generateRandomAvatarUrl() {
        return "%s%s%s%s?seed=%s&size=%d".formatted(
                diceBearProperties.host(),
                diceBearProperties.apiVersion(),
                diceBearProperties.styleName(),
                diceBearProperties.format(),
                seed,
                diceBearProperties.size());
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