package school.faang.user_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import school.faang.user_service.service.s3.SimpleMultipartFile;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class DiceBearClient {

    @Value("${dice.bear.client.baseUrl}")
    private String baseUrl;
    @Value("${avatar.default.template}")
    private String defaultAvatarTemplate;
    private final HttpClient httpClient;
    private static final Random random = new Random();

    public MultipartFile generateRandomAvatar() {
        String randomStyle = getRandomStyle().getStyleName();
        String randomSeed = UUID.randomUUID().toString().substring(0, 8);
        UriComponents uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .pathSegment(randomStyle, "svg")
                .queryParam("seed", randomSeed)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.toUri())
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();
        return sendRequest(request, randomSeed);
    }

    private MultipartFile sendRequest(HttpRequest request, String randomSeed) {
        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            boolean isSuccessful = response.statusCode() == 200;
            if (isSuccessful) {
                byte[] avatarData = response.body();
                return new SimpleMultipartFile(
                        "avatar",
                        "avatar_" + randomSeed + ".svg",
                        "image/svg+xml",
                        avatarData
                );
            }
        } catch (Exception e) {
            log.error("An error occurred while downloading the file");
        }
        return generateDefaultAvatar();
    }

    private MultipartFile generateDefaultAvatar() {
        String defaultSvg = defaultAvatarTemplate.formatted(generateRandomColor());

        return new SimpleMultipartFile(
                "avatar",
                "avatar_default.svg",
                "image/svg+xml",
                defaultSvg.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String generateRandomColor() {
        return String.format("%06x", random.nextInt(0xFFFFFF));
    }


    private DiceBearStyles getRandomStyle() {
        DiceBearStyles[] diceBearStyles = DiceBearStyles.values();
        int index = random.nextInt(diceBearStyles.length);
        return diceBearStyles[index];
    }
}
