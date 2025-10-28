package school.faang.user_service.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private final S3Client amazonS3;


    public void saveToFileStorage(MultipartFile multipartFile, String key) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentLength(multipartFile.getSize())
                    .contentType(multipartFile.getContentType())
                    .metadata(Map.of("filename", Objects.requireNonNull(multipartFile.getOriginalFilename())))
                    .build();
            try (InputStream inputStream = multipartFile.getInputStream()) {
                amazonS3.putObject(request, RequestBody.fromInputStream(inputStream, multipartFile.getSize()));
            }

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public MultipartFile downloadAvatar(String key) {
        S3Object s3Object  = amazonS3.getObject(bucketName, key);
        return s3
    }
}
