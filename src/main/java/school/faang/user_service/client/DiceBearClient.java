package school.faang.user_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Slf4j
public class DiceBearClient {

    private static final String BASE_URL = "https://api.dicebear.com/7.x";

    public byte[] generateAvatarPng(String style) throws Exception {
        String url = String.format("%s/%s/png", BASE_URL, style);
        log.debug("Generating avatar from DiceBear: {}", url);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }
}
