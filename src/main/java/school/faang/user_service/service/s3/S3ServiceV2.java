package school.faang.user_service.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.FileException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceV2 {

    private final S3Client amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public byte[] downloadFileAsBytes(String key) {
        try {
            return amazonS3.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            ).asByteArray();
        } catch (S3Exception e) {
            throw new FileException(String.format("Error downloading file from S3, key = '%s'", key));
        }
    }

    public HeadObjectResponse getFileMetadata(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            return amazonS3.headObject(headObjectRequest);
        } catch (S3Exception e) {
            throw new FileException(String.format("File not found in S3: '%s'", key));
        }
    }

    public String getFileContentType(String key) {
        try {
            HeadObjectResponse metadata = getFileMetadata(key);
            return metadata.contentType();
        } catch (Exception e) {
            log.warn("Falling back to default content-type for key={}", key);
            return "image/png";
        }
    }
}
