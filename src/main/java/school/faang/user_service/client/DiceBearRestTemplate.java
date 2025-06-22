package school.faang.user_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class DiceBearRestTemplate {

    private final RestTemplate restTemplate;

    @Value("${services.dicebear.url}")
    private String baseUrl;

    public byte[] getAvatar(String version, String style, String format, String seed) {
        String url = String.format("%s/%s/%s/%s?seed=%s", baseUrl, version, style, format, seed);

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Ошибка при запросе к DiceBear: " + response.getStatusCode());
        }
    }
}