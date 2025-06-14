package school.faang.user_service.service.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.file.FileUploadException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Retryable(
            retryFor = {AmazonServiceException.class, AmazonClientException.class, SdkClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void uploadFile(byte[] file, String fileKey, MediaType type) {
        int fileSize = file.length;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(type.toString());
        try (InputStream inputStream = new ByteArrayInputStream(file)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, inputStream, objectMetadata);
            amazonS3.putObject(putObjectRequest);
        } catch (Exception ex) {
            log.error("Error uploading file to S3: {}", ex.getMessage(), ex);
            throw new FileUploadException(String.format("Error uploading file to S3: %s", fileKey));
        }
        log.info("uploading file to S3 with key: {}", fileKey);
    }
}

