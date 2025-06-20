package school.faang.user_service.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.resource.S3FileDto;
import school.faang.user_service.exception.common.FileException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import static school.faang.user_service.util.LogsConstants.DELETION_FAILED;
import static school.faang.user_service.util.LogsConstants.DOWNLOAD_FAILED;
import static school.faang.user_service.util.LogsConstants.UPLOAD_FAILED;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final S3Client client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(String folder, MultipartFile file) {
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentLength(file.getSize())
                    .contentType(file.getContentType())
                    .metadata(Map.of("filename", Objects.requireNonNull(file.getOriginalFilename())))
                    .build();
            try (InputStream inputStream = file.getInputStream()) {
                client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
            }
        } catch (Exception e) {
            log.error(UPLOAD_FAILED, e);
            throw new FileException(UPLOAD_FAILED);
        }
        return key;
    }

    public void deleteFile(String key) {
        try {
            client.deleteObject(request -> request.bucket(bucketName).key(key));
        } catch (Exception e) {
            log.error(DELETION_FAILED, e);
            throw new FileException(DELETION_FAILED);
        }
    }

    public S3FileDto downloadFile(String key) {
        try {
            ResponseInputStream<GetObjectResponse> s3Object = client.getObject(request -> request.bucket(bucketName).key(key));
            Resource streamResource = new InputStreamResource(s3Object);

            HeadObjectResponse metadata = client.headObject(request -> request.bucket(bucketName).key(key));
            return S3FileDto.builder()
                    .fileName(metadata.metadata().get("filename"))
                    .contentType(metadata.contentType())
                    .contentLength(metadata.contentLength())
                    .resource(streamResource)
                    .build();
        } catch (Exception e) {
            log.error(DOWNLOAD_FAILED, e);
            throw new FileException(DOWNLOAD_FAILED);
        }
    }
}