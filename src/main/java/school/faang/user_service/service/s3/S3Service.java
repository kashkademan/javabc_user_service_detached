package school.faang.user_service.service.s3;

import school.faang.user_service.dto.s3.S3FileDto;
import school.faang.user_service.exception.s3.FileException;
import school.faang.user_service.exception.s3.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final S3Presigner presigner;
    private final S3Client client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.presignedUrl.ttl:1}")
    private int presignedUrlTtl;

    public String uploadFile(String folder, MultipartFile file) {
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentLength(file.getSize())
                    .contentType(file.getContentType())
                    .build();
            client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileException("File uploading failed");
        }

        return key;
    }

    public void deleteFile(String key) {
        try {
            client.deleteObject(request -> request.bucket(bucketName).key(key));
        } catch (StorageException e) {
            log.error("Failed to delete file from S3: ", e);
            throw new StorageException("File uploading failed");
        }
    }

    public S3FileDto downloadFile(String key) {
        try {
            ResponseInputStream<GetObjectResponse> s3Object = client.getObject(
                    request -> request.bucket(bucketName).key(key)
            );
            Resource streamResource = new InputStreamResource(s3Object);

            HeadObjectResponse metadata = client.headObject(
                    request -> request.bucket(bucketName).key(key)
            );

            return S3FileDto.builder()
                    .fileName(metadata.metadata().get("filename"))
                    .contentType(metadata.contentType())
                    .contentLength(metadata.contentLength())
                    .resource(streamResource)
                    .build();
        } catch (StorageException e) {
            log.error("Не получилось загрузить файл из хранилища: ", e);
            throw new StorageException("Произошла ошибка при попытке загрузить файл из хранилища.");
        }
    }

    public String generatePresignedUrl(String key) {
        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(presignedUrlTtl))
                    .getObjectRequest(b -> b.bucket(bucketName).key(key))
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Ошибка при генерации presigned URL для ключа '{}': {}", key, e.getMessage(), e);
            throw new StorageException("Не удалось создать presigned URL");
        }
    }
}