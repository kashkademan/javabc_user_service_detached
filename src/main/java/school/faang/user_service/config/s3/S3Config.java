package school.faang.user_service.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    @Value("${services.s3.accessKey}")
    private String accessKey;
    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("http://${services.s3.host}:${services.s3.post}")
    private String url;

    @Bean
    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, "us-west-2"))
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
