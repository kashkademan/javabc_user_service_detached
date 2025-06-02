package school.faang.user_service.config.context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.InputStream;
import java.util.List;

public class MinioStorage {
    private final AmazonS3 s3Client;

    public MinioStorage(String endPoint, String accessKey, String secretKey, String region) {
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        endPoint,
                        region
                ))
                .withPathStyleAccessEnabled(true)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withClientConfiguration(new ClientConfiguration()
                        .withMaxConnections(100)
                        .withConnectionTimeout(10 * 1000))
                .build();
    }

    public void createBucket(String bucketName) {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
        }
    }

    public void uploadFile(String bucketName, String objectName, InputStream inputStream) {
        s3Client.putObject(bucketName, objectName, inputStream, new ObjectMetadata());
    }

    public S3Object downloadFile(String bucketName, String objectName) {
        return s3Client.getObject(bucketName, objectName);
    }

    public void deleteFile(String bucketName, String objectName) {
        s3Client.deleteObject(bucketName, objectName);
    }

    public List<S3ObjectSummary> listFiles(String bucketName) {
        return s3Client.listObjects(bucketName).getObjectSummaries();
    }

    public void shutdown() {
        s3Client.shutdown();
    }

}
