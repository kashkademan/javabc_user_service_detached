package school.faang.user_service.service.avatar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Сервис для генерации аватаров с использованием DiceBear API.
 * <p>
 * Предоставляет функциональность для создания уникальных стилизованных аватаров
 * на основе переданных параметров через интеграцию с внешним сервисом DiceBear.
 * </p>
 *
 * @author Linempy
 * @since 03.08.2025
 */
@Service
public class DiceBearAvatarService {

    private final WebClient webClient;
    private final Random random = new Random();

    private static final List<String> STYLES = List.of(
            "adventurer",
            "avataaars",
            "big-ears",
            "bottts",
            "croodles",
            "micah",
            "pixel-art"
    );

    public DiceBearAvatarService(@Value("${generate-ava.dice.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public byte[] generateRandomAvatar() {
        String style = STYLES.get(random.nextInt(STYLES.size()));

        String seed = UUID.randomUUID().toString();

        return webClient.get()
                .uri("/{style}/png/seed={seed}", style, seed)
                .accept(MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
}