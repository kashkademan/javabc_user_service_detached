package school.faang.user_service.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.entity.resource.ResourceStatus;
import school.faang.user_service.entity.resource.ResourceType;
import school.faang.user_service.exception.FileException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        try {
            return uploadFile(
                    file.getInputStream(),
                    file.getSize(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    folder
            );
        } catch (IOException e) {
            String errorMessage = "Failed to read file: " + file.getOriginalFilename();
            log.error(errorMessage);
            throw new FileException(errorMessage);
        }
    }

    @Override
    public Resource uploadFile(byte[] fileData, String filename, String contentType, String folder) {
        return uploadFile(
                new ByteArrayInputStream(fileData),
                fileData.length,
                filename,
                contentType,
                folder
        );
    }

    private Resource uploadFile(InputStream inputStream, long fileSize,
                                String filename, String contentType, String folder) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(contentType);

        String key = "%s/%d%d%d%s".formatted(folder, System.currentTimeMillis(),
                ThreadLocalRandom.current().nextInt(1000, 9999), fileSize, filename);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key,
                    inputStream, objectMetadata);
            s3Client.putObject(putObjectRequest);
            log.info("File {} has been uploaded into bucket {}", filename, bucketName);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileException("File %s upload failed".formatted(filename));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(fileSize))
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(contentType))
                .name(filename)
                .build();
    }

    @Override
    public void deleteFile(String fileKey) {
        try {
            s3Client.deleteObject(bucketName, fileKey);
            log.info("File {} has been deleted", fileKey);
        } catch (AmazonServiceException e) {
            log.error("S3 service error deleting file {}: {}", fileKey, e.getMessage());
            throw new FileException("File %s deletion failed - storage service error".formatted(fileKey));
        } catch (SdkClientException e) {
            log.error("Client error deleting file {}: {}", fileKey, e.getMessage());
            throw new FileException("File %s deletion failed - connection issue".formatted(fileKey));
        }
    }

    @Override
    public MultipartFile getFile(String key) {
        try (S3Object s3ClientObject = s3Client.getObject(bucketName, key);
             InputStream inputStream = s3ClientObject.getObjectContent();
        ) {
            byte[] content = com.amazonaws.util.IOUtils.toByteArray(inputStream);
            MockMultipartFile fileToReturn =
                    new MockMultipartFile(
                            key,
                            key,
                            s3ClientObject.getObjectMetadata().getContentType(),
                            content);

            log.info("Got file by key {}", key);
            return fileToReturn;
        } catch (AmazonS3Exception e) {
            String errorMessage = "File by key %s not found in S3".formatted(key);
            log.error(errorMessage);
            throw new FileException(errorMessage);
        } catch (IOException e) {
            String errorMessage = "Error reading file by key %s".formatted(key);
            log.error(errorMessage);
            throw new FileException(errorMessage);
        }
    }
}