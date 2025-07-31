package school.faang.user_service.avatar.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Реализация хранилища файлов на основе Yandex S3.
 * <p>
 * Этот класс предоставляет методы для загрузки файлов в Yandex S3 хранилище.
 * Он использует {@link S3Client} для взаимодействия с API Yandex S3.
 * </p>
 *
 * @author agent
 * @since 26.07.2025
 */
@Component
@RequiredArgsConstructor
public class YandexS3FileStorage implements FileStorage {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.endpoint}")
    private String endpoint;

    /**
     * Загружает файл в Yandex S3 хранилище и возвращает публичный URL.
     *
     * @param path путь внутри бакета (например, avatars/user123.png)
     * @param content поток данных файла
     * @param contentLength длина файла
     * @param contentType тип контента (например, image/png)
     * @return URL, по которому можно получить файл
     */
    public String upload(String path, InputStream content, long contentLength, String contentType) {
        if (contentLength < 0) {
            try {
                byte[] bytes = content.readAllBytes();
                contentLength = bytes.length;
                content = new ByteArrayInputStream(bytes);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read content stream", e);
            }
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(content, contentLength));

        return String.format("%s/%s/%s", endpoint, bucket, path);
    }
}