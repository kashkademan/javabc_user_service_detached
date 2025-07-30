package school.faang.user_service.avatar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Конфигурационный класс для настройки клиента Amazon S3.
 * <p>
 * Этот класс предоставляет конфигурацию для создания клиента S3, используя параметры,
 * заданные в свойствах приложения. Он настраивает регион, конечную точку и учетные данные
 * для доступа к хранилищу S3.
 * </p>
 *
 * @author agent
 * @since 25.07.2025
 */
@Configuration
@Profile("!test")
public class S3Config {

    @Value("${cloud.aws.s3.access-key}")
    private String accessKey;

    @Value("${cloud.aws.s3.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.region}")
    private String region;

    @Value("${cloud.aws.s3.endpoint}")
    private String endpoint;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}