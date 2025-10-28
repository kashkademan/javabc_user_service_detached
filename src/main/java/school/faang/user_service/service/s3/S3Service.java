package school.faang.user_service.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

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
            throw new FileException(String.format("Error generating random avatar for user!Key - %s", key));
        }
    }

    public byte[] downloadAvatarAsBytes(String key) {
        return downloadFileAsBytes(key);
    }

    public byte[] downloadFileAsBytes(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = amazonS3.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();

        } catch (S3Exception e) {
            throw new FileException(String.format("Error downloading profile picture for user! Key -", key));
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
            throw new RuntimeException("File not found in S3: " + key, e);
        }
    }

    public String getFileContentType(String key) {
        try {
            HeadObjectResponse metadata = getFileMetadata(key);
            return metadata.contentType();
        } catch (Exception e) {
            return "image/svg+xml"; // fallback для аватаров
        }
    }
}
