package school.faang.user_service.avatar.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

/**
 * Класс, представляющий файл аватара.
 * <p>
 * Этот класс инкапсулирует содержимое файла аватара, его длину и тип содержимого.
 * </p>
 *
 * @author agent
 * @since 26.07.2025
 */
@Getter
@RequiredArgsConstructor
public class AvatarFile {
    private final InputStream content;
    private final long contentLength;
    private final String contentType;
}