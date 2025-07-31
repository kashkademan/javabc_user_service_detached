package school.faang.user_service.avatar.storage;

import java.io.InputStream;

/**
 * Интерфейс для работы с хранилищем файлов.
 * <p>
 * Реализации этого интерфейса должны предоставлять методы для загрузки файлов в хранилище
 * и получения публичных URL для доступа к этим файлам.
 * </p>
 *
 * @author agent
 * @since 26.07.2025
 */
public interface FileStorage {

    /**
     * Загружает файл в хранилище и возвращает публичный URL.
     *
     * @param path путь внутри хранилища (например, avatars/user123.png)
     * @param content поток данных файла
     * @param contentLength длина файла
     * @param contentType тип контента (например, image/png)
     * @return URL, по которому можно получить файл
     */
    String upload(String path, InputStream content, long contentLength, String contentType);
}