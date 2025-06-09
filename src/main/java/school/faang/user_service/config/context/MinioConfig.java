package school.faang.user_service.config.context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${minio.end_point}")
    private String END_POINT;

    @Value("${minio.access_key}")
    private String ACCESS_KEY;

    @Value("${minio.secret_key}")
    private String SECRET_KEY;

    @Value("${minio.region}")
    private String REGION;

    @Value("${minio.content_length}")
    private Long CONTENT_LENGTH;

    @Bean
    public AmazonS3 amazonS3() {
        if (END_POINT == null || END_POINT.isBlank()) {
            throw new IllegalArgumentException("Endpoint cannot be null or empty");
        }
        if (ACCESS_KEY == null || ACCESS_KEY.isBlank()) {
            throw new IllegalArgumentException("Access key cannot be null or empty");
        }
        if (SECRET_KEY == null || SECRET_KEY.isBlank()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        if (REGION == null || REGION.isBlank()) {
            throw new IllegalArgumentException("Region cannot be null or empty");
        }

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        END_POINT,
                        REGION
                ))
                .withPathStyleAccessEnabled(true)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                .withClientConfiguration(new ClientConfiguration()
                        .withMaxConnections(100)
                        .withConnectionTimeout(10 * 1000))
                .build();
    }

    @Bean
    public TransferManager transferManager(AmazonS3 amazonS3){
        return TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .withMultipartUploadThreshold(CONTENT_LENGTH)
                .build();
    }
}
