package school.faang.user_service.service.avatar;



import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.minios3.FileUploadResponse;
import school.faang.user_service.exception.FileStorageException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final S3Client s3Client;
    private static final String ALLOWED_FILENAME_CHARS_PATTERN = "[^a-zA-Z0-9._-]";
    private static final int MAX_FILENAME_LENGTH = 255;

    @Value("${services.s3.user-avatars.bucket-name}")
    private String bucketName;

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @PostConstruct
    public void validateConfiguration() {
        if (StringUtils.isBlank(bucketName)) {
            throw new IllegalStateException("S3 bucket name is not configured");
        }
        ensureBucketExists();
    }

    @Transactional
    public FileUploadResponse uploadFile(byte[] fileData, String fileName, String contentType) {
        validateFileInput(fileData, fileName, contentType);
        String objectKey = generateObjectKey(fileName);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength((long) fileData.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));

            String fileUrl = generatePublicUrl(objectKey);
            log.info("File {} successfully uploaded to bucket {}", fileName, bucketName);

            return FileUploadResponse.builder()
                    .fileId(objectKey)
                    .fileName(fileName)
                    .fileUrl(fileUrl)
                    .size(fileData.length)
                    .contentType(contentType)
                    .uploadTime(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error uploading {} file to S3", fileName, e);
            throw new FileStorageException("Failed to upload file");
        }
    }

    @Transactional
    public void deleteFile(String fileId) {
        if (fileId == null || fileId.isBlank()) {
            return;
        }

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileId)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File {} successfully removed from bucket {}", fileId, bucketName);
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
            throw new FileStorageException("Failed to delete file");
        }
    }

    public byte[] downloadFile(String fileId) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileId)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (Exception e) {
            log.error("Error loading file {} from S3", fileId, e);
            throw new FileStorageException("Failed to download file");
        }
    }

    public boolean fileExists(String fileId) {
        if (fileId == null || fileId.isBlank()) {
            return false;
        }

        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileId)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            log.error("Error checking the existence of a file", e);
            return false;
        }
    }

    private String generateObjectKey(String fileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString();
        String safeFileName = FilenameUtils.getName(fileName)
                .replaceAll(ALLOWED_FILENAME_CHARS_PATTERN, "_");

        if (safeFileName.length() > MAX_FILENAME_LENGTH) {
            safeFileName = safeFileName.substring(0, MAX_FILENAME_LENGTH);
        }

        return String.format("%s/%s-%s", timestamp, uuid, safeFileName);
    }

    private String generatePublicUrl(String objectKey) {
        try {
            URL url = s3Client.utilities().getUrl(builder -> builder
                    .bucket(bucketName)
                    .key(objectKey));
            return url.toString();
        } catch (Exception e) {
            log.error("Error generating URL for file {}", objectKey, e);
            return String.format("%s/%s/%s", endpoint, bucketName, objectKey);
        }
    }

    private void validateFileInput(byte[] fileData, String fileName, String contentType) {
        if (fileData == null || fileData.length == 0) {
            throw new IllegalArgumentException("File data cannot be empty");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("The file name cannot be empty");
        }
        if (StringUtils.isBlank(contentType)) {
            throw new IllegalArgumentException("Content type cannot be empty");
        }
    }

    private void ensureBucketExists() {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.headBucket(headBucketRequest);
            log.info("Bucket {} exists", bucketName);

        } catch (NoSuchBucketException e) {
            log.info("Creating a bucket: {}", bucketName);
            try {
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();
                s3Client.createBucket(createBucketRequest);
            } catch (Exception createException) {
                log.error("Error creating bucket {}", bucketName, createException);
                throw new FileStorageException("Storage access error");
            }
        } catch (Exception e) {
            log.error("Error accessing bucket {}", bucketName, e);
            throw new FileStorageException("Storage access error");
        }
    }
}
