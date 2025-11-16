package school.faang.user_service.service.avatar;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gradle.internal.impldep.com.amazonaws.services.s3.AmazonS3;
import org.gradle.internal.impldep.com.amazonaws.services.s3.model.ObjectMetadata;
import org.gradle.internal.impldep.com.amazonaws.services.s3.model.S3Object;
import org.gradle.internal.impldep.com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.gradle.internal.impldep.org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.minios3.FileUploadResponse;
import school.faang.user_service.exception.FileStorageException;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final AmazonS3 amazonS3;
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
        ObjectMetadata metadata = createMetadata(fileData, contentType);

        try {
            amazonS3.putObject(bucketName, objectKey,
                    new ByteArrayInputStream(fileData), metadata);
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
            amazonS3.deleteObject(bucketName, fileId);
            log.info("File {} successfully removed from bucket {}", fileId, bucketName);
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
            throw new FileStorageException("Failed to delete file");
        }
    }

    public byte[] downloadFile(String fileId) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, fileId);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] file = inputStream.readAllBytes();
            inputStream.close();
            return file;
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
            return amazonS3.doesObjectExist(bucketName, fileId);
        } catch (Exception e) {
            log.error("Error checking the existence of a file", e);
            return false;
        }
    }

    private String generateObjectKey(String fileName) {

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString();
        String safeFileName = FilenameUtils.getName(fileName)
                .replaceAll(ALLOWED_FILENAME_CHARS_PATTERN, "_");
        if (safeFileName.length() > MAX_FILENAME_LENGTH ) {
            safeFileName = safeFileName.substring(0,
                    Math.min(MAX_FILENAME_LENGTH, FilenameUtils.getName(safeFileName).length()));
        }
        return String.format("%s/%s-%s", timestamp, uuid, safeFileName);
    }

    private String generatePublicUrl(String objectKey) {
        URL url = amazonS3.getUrl(bucketName, objectKey);
        return url.toString();
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
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                log.info("Creating a bucket: {}", bucketName);
                amazonS3.createBucket(bucketName);
            }
        } catch (Exception e) {
            log.error("Error creating bucket {}", bucketName, e);
            throw new FileStorageException("Storage access error");
        }
    }

    private ObjectMetadata createMetadata(byte[] fileData, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileData.length);
        metadata.setContentType(contentType);
        return metadata;
    }
}
