package school.faang.user_service.service.storage.minio;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.StorageException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для {@link MinioFileStorage}
 *
 * @author Linempy
 * @since 06.08.2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование класса для работы с MinIO")
class MinioFileStorageTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioFileStorage minioFileStorage;

    @Test
    @DisplayName("upload должен успешно загружать файл в MinIO при корректных параметрах")
    void testUploadSuccess() throws Exception {
        byte[] fileBytes = new byte[]{1, 2, 3};
        String bucketName = "test-bucket";
        String objectKey = "test-key";
        String contentType = "image/png";

        minioFileStorage.upload(fileBytes, bucketName, objectKey, contentType);

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("upload должен выбрасывать StorageException при ошибке MinIO")
    void testUploadShouldThrowError() throws Exception {
        byte[] fileBytes = new byte[]{1, 2, 3};
        String bucketName = "test-bucket";
        String objectKey = "test-key";
        String contentType = "image/png";

        doThrow(new RuntimeException("MinIO error"))
                .when(minioClient)
                .putObject(any(PutObjectArgs.class));

        StorageException exception = assertThrows(StorageException.class,
                () -> minioFileStorage.upload(fileBytes, bucketName, objectKey, contentType)
        );
        assertEquals("Ошибка в загрузки файла в MinIO", exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception.getCause());
    }

    @Test
    @DisplayName("download должен возвращать байты файла при успешном скачивании")
    void testDownloadShouldReturnFileBytes() throws Exception {
        byte[] expectedBytes = new byte[]{1, 2, 3};
        String bucketName = "test-bucket";
        String objectKey = "test-key";

        GetObjectResponse response = mock(GetObjectResponse.class);
        when(response.readAllBytes()).thenReturn(expectedBytes);
        doNothing().when(response).close();

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);

        byte[] result = minioFileStorage.download(bucketName, objectKey);

        assertArrayEquals(expectedBytes, result);
        verify(response).close();
    }

    @Test
    @DisplayName("download должен выбрасывать StorageException при ошибке MinIO")
    void testDownloadShouldThrowError() throws Exception {
        String bucketName = "test-bucket";
        String objectKey = "test-key";

        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("MinIO error"));

        StorageException exception = assertThrows(StorageException.class,
                () -> minioFileStorage.download(bucketName, objectKey)
        );

        assertEquals("Ошибка в скачивании файла из MinIO", exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception.getCause());
    }
}