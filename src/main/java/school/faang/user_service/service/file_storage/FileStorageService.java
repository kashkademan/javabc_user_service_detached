package school.faang.user_service.service.file_storage;

import school.faang.user_service.exception.StorageException;

/**
 * Интерфейс для работы с файловым хранилищем.
 * Определяет базовые операции для загрузки и скачивания файлов.
 *
 * @author Linempy
 * @since 03.08.2025
 */
public interface FileStorageService {

    /**
     * Загружает файл в хранилище
     *
     * @param fileBytes массив байтов файла для загрузки
     * @param bucketName имя бакета/контейнера в хранилище
     * @param objectKey уникальный ключ объекта в хранилище
     * @param contentType MIME-тип содержимого файла
     *
     * @throws StorageException при ошибках загрузки файла
     */
    void upload(byte[] fileBytes, String bucketName, String objectKey, String contentType) throws StorageException;

    /**
     * Скачивает файл из хранилища
     *
     * @param bucketName имя бакета/контейнера в хранилище
     * @param objectKey уникальный ключ объекта в хранилище
     *
     * @return массив байтов содержимого файла
     * @throws StorageException при ошибках скачивания файла
     */
    byte[] download(String bucketName, String objectKey) throws StorageException;
}