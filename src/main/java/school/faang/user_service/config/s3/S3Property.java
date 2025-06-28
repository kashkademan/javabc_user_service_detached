package school.faang.user_service.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("s3")
record S3Property(
        String accessKey,
        String secretKey,
        String endpoint,
        String region
) {
}
