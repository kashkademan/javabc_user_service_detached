package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
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

        String key = "%s/%d%d%s".formatted(folder, System.currentTimeMillis(),
                ThreadLocalRandom.current().nextInt(1000, 9999), filename);

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
}