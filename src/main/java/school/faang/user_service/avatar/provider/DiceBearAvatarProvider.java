package school.faang.user_service.avatar.provider;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.AvatarGenerateException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Провайдер аватаров, использующий сервис DiceBear для генерации аватаров.
 * <p>
 * Этот класс реализует интерфейс {@link AvatarProvider} и предоставляет методы для генерации аватаров
 * с использованием API DiceBear. Поддерживаются различные стили и форматы аватаров.
 * </p>
 * <p>
 * Доступные стили аватаров:
 * <ul>
 *     <li><b>pixel-art</b>: Пиксельный стиль</li>
 *     <li><b>open-peeps</b>: Стиль открытых персонажей</li>
 *     <li><b>avataaars</b>: Стиль аватаров</li>
 *     <li><b>adventurer</b>: Стиль приключенцев</li>
 *     <li><b>big-ears</b>: Стиль с большими ушами</li>
 * </ul>
 * </p>
 * <p>
 * Доступные форматы аватаров:
 * <ul>
 *     <li><b>svg</b>: Векторный формат</li>
 *     <li><b>png</b>: Растровый формат</li>
 * </ul>
 * </p>
 *
 * @author agent
 * @since 26.07.2025
 */
@Component
public class DiceBearAvatarProvider implements AvatarProvider {

    private static final String STYLE = "pixel-art";
    private static final String FORMAT = "svg";
    private static final String API_URL = "https://api.dicebear.com/7.x/%s/%s?seed=%s";

    /**
     * Генерирует файл аватара на основе уникального ключа.
     * <p>
     * Этот метод использует API DiceBear для генерации аватара в заданном стиле и формате.
     * </p>
     *
     * @param key уникальный ключ (например, username или UUID)
     * @return объект {@link AvatarFile}, содержащий сгенерированный аватар
     */
    @Override
    public AvatarFile generate(String key) {

        String url = String.format(API_URL, STYLE, FORMAT, key);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            try (InputStream content = connection.getInputStream()) {
                byte[] bytes = content.readAllBytes();
                long contentLength = bytes.length;
                String contentType = connection.getContentType();

                return new AvatarFile(new ByteArrayInputStream(bytes), contentLength, contentType);
            }
        } catch (IOException e) {
            throw new AvatarGenerateException("Failed to generate avatar from DiceBear API", e);
        }
    }
}