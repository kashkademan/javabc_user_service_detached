package school.faang.user_service.service.storage.minio;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.StorageException;
import school.faang.user_service.service.storage.FileStorageService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Реализация {@link FileStorageService} для работы с объектным хранилищем MinIO.
 * <p>
 * Предоставляет методы для загрузки и скачивания файлов из MinIO хранилища.
 * Все операции выполняются через клиент {@link MinioClient}.
 * </p>
 *
 * @author Linempy
 * @since 03.08.2025
 */
@Service
@RequiredArgsConstructor
public class MinioFileStorage implements FileStorageService {

    private final MinioClient client;

    @Override
    public void upload(byte[] fileBytes, String bucketName, String objectKey, String contentType)
            throws StorageException {
        try (InputStream fileStream = new ByteArrayInputStream(fileBytes)) {
            PutObjectArgs putObject = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(fileStream, fileBytes.length, -1)
                    .contentType(contentType)
                    .build();

            client.putObject(putObject);
        } catch (Exception e) {
            throw new StorageException("Ошибка в загрузки файла в MinIO", e);
        }
    }

    @Override
    public byte[] download(String bucketName, String objectKey) throws StorageException {
        try {
            GetObjectArgs getObject = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build();
            try (InputStream stream = client.getObject(getObject)) {
                return stream.readAllBytes();
            }
        } catch (Exception e) {
            throw new StorageException("Ошибка в скачивании файла из MinIO", e);
        }
    }

}