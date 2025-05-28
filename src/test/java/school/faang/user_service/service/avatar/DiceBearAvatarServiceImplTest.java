package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.config.avatar.DiceBearConfig;
import school.faang.user_service.entity.User;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiceBearAvatarServiceImplTest {

    private AvatarService diceBearAvatarService;
    private DiceBearConfig diceBearConfig;
    private static final String DEFAULT_SEED = "defaultUserSeed";

    @BeforeEach
    public void setUp() {
        diceBearConfig = new DiceBearConfig();
        diceBearConfig.setApiUrl("https://api.dicebear.com/9.x");
        diceBearConfig.setStyle("pixel-art");
        diceBearAvatarService = new DiceBearAvatarServiceImpl(diceBearConfig);
    }

    @Test
    @DisplayName("Генерация аватара: когда имя пользователя задано, возвращается корректный URL")
    void givenUserWithValidUsername_whenGenerateAvatarUrl_thenReturnExpectedUrl() {
        User user = new User();
        user.setUsername("TestUser");
        String generatedUrl = diceBearAvatarService.generateAvatarUrl(user);
        String expectedSeed = URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
        String expectedUrl = String.format(
                "%s/%s/svg?seed=%s", diceBearConfig.getApiUrl(), diceBearConfig.getStyle(), expectedSeed);
        assertEquals(expectedUrl, generatedUrl);
    }

    @Test
    @DisplayName("Генерация аватара: когда имя содержит пробелы, происходит правильное кодирование")
    void givenUserWithUsernameContainingSpaces_whenGenerateAvatarUrl_thenReturnEncodedUrl() {
        User user = new User();
        user.setUsername("Test User");
        String generatedUrl = diceBearAvatarService.generateAvatarUrl(user);
        String expectedSeed = URLEncoder.encode("Test User", StandardCharsets.UTF_8);
        String expectedUrl = String.format(
                "%s/%s/svg?seed=%s", diceBearConfig.getApiUrl(), diceBearConfig.getStyle(), expectedSeed);
        assertEquals(expectedUrl, generatedUrl);
    }

    @Test
    @DisplayName("Генерация аватара: когда имя пользователя отсутствует (null), используется значение по умолчанию")
    void givenUserWithoutUsername_whenGenerateAvatarUrl_thenUseDefaultSeed() {
        User user = new User();
        user.setUsername(null);
        String generatedUrl = diceBearAvatarService.generateAvatarUrl(user);
        String expectedSeed = URLEncoder.encode(DEFAULT_SEED, StandardCharsets.UTF_8);
        String expectedUrl = String.format(
                "%s/%s/svg?seed=%s", diceBearConfig.getApiUrl(), diceBearConfig.getStyle(), expectedSeed);
        assertEquals(expectedUrl, generatedUrl);
    }

    @Test
    @DisplayName("Генерация аватара: когда имя пользователя пустая строка, используется значение по умолчанию")
    void givenUserWithEmptyUsername_whenGenerateAvatarUrl_thenUseDefaultSeed() {
        User user = new User();
        user.setUsername("");
        String generatedUrl = diceBearAvatarService.generateAvatarUrl(user);
        String expectedSeed = URLEncoder.encode(DEFAULT_SEED, StandardCharsets.UTF_8);
        String expectedUrl = String.format(
                "%s/%s/svg?seed=%s", diceBearConfig.getApiUrl(), diceBearConfig.getStyle(), expectedSeed);
        assertEquals(expectedUrl, generatedUrl);
    }

    @Test
    @DisplayName("Генерация аватара: когда объект User равен null, используется значение по умолчанию")
    void givenNullUser_whenGenerateAvatarUrl_thenUseDefaultSeed() {
        String generatedUrl = diceBearAvatarService.generateAvatarUrl(null);
        String expectedSeed = URLEncoder.encode(DEFAULT_SEED, StandardCharsets.UTF_8);
        String expectedUrl = String.format(
                "%s/%s/svg?seed=%s", diceBearConfig.getApiUrl(), diceBearConfig.getStyle(), expectedSeed);
        assertEquals(expectedUrl, generatedUrl);
    }
}