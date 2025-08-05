package school.faang.user_service.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки клиента MinIO.
 * <p>
 * Создает и настраивает бины для работы с объектным хранилищем MinIO.
 * Параметры подключения берутся из конфигурации приложения
 * </p>
 *
 * @author Linempy
 * @since 03.08.2025
 */
@Configuration
public class MinioConfig {

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.access-key}")
    private String accessKey;

    @Value("${services.s3.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}