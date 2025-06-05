package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
// TODO: надо ли
//@ConditionalOnProperty(value = "service.s3.isMocked", havingValue = "false")
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(byte[] file, String folder) {
        int fileSize = file.length;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType("svg");
        // TODO: другой ключ
        String key = folder + "/" + UUID.randomUUID() + ".svg";
        try (InputStream inputStream = new ByteArrayInputStream(file)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
            amazonS3.putObject(putObjectRequest);
        } catch (Exception ex) {
            log.error("Error uploading file to S3: {}", ex.getMessage());
            // TODO: другой тип исключения
            throw new RuntimeException("Failed to upload file", ex);
        }
        return key;
    }
}

