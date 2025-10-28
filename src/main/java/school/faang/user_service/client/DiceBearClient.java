package school.faang.user_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class DiceBearClient {
    private static final String BASE_URL = "https://api.dicebear.com/7.x";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Random random = new Random();

    public static final String[] STYLES = {
            "adventurer", "adventurer-neutral", "avataaars", "big-ears",
            "bottts", "croodles", "fun-emoji", "icons", "identicon",
            "lorelei", "micah", "miniavs", "open-peeps", "personas",
            "pixel-art", "shapes", "thumbs"
    };


    public static MultipartFile generateRandomAvatar() {
        try {
            String randomStyle = STYLES[random.nextInt(STYLES.length)];
            String randomSeed = UUID.randomUUID().toString().substring(0, 8);
            String avatarUrl = String.format("%s/%s/svg?seed=%s", BASE_URL, randomStyle, randomSeed);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(avatarUrl))
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                byte[] avatarData = response.body();

                return new MockMultipartFile(
                        "avatar",
                        "avatar_" + randomSeed + ".svg",
                        "image/svg+xml",
                        avatarData
                );
            }
        } catch (Exception e) {
            log.error("An error occurred while downloading the file");
        }
        throw new RuntimeException();
    }

}
