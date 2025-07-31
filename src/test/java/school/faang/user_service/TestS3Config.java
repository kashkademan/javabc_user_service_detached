package school.faang.user_service;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
@Profile("test")
public class TestS3Config {
    @Bean
    public S3Client s3Client() {
        return Mockito.mock(S3Client.class);
    }
}