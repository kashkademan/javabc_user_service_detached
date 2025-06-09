package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.FileLoadException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {
    private final AmazonS3 amazonS3;
    private final TransferManager transferManager;

    @Value("${minio.content_length}")
    private Long CONTENT_LENGTH;

    @Value("${minio.bucket}")
    private String BUCKET_NAME;

    public void createBucket() {
        if (!amazonS3.doesBucketExistV2(BUCKET_NAME)) {
            amazonS3.createBucket(BUCKET_NAME);
        }
    }


    public void uploadFile(String object, String objectName, Long contentLength) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(object.getBytes())) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            if (contentLength > CONTENT_LENGTH) {
                Upload upload = transferManager.upload(BUCKET_NAME, objectName, inputStream, metadata);

                upload.waitForCompletion();
            }
            else {
                amazonS3.putObject(BUCKET_NAME, objectName, inputStream, new ObjectMetadata());
            }
        } catch (InterruptedException e) {
            log.error("Download error", e);
            throw new FileLoadException("Download error");
        }
    }

    public void uploadFile(byte[] object, String objectName, Long contentLength) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(object)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            if (contentLength > CONTENT_LENGTH) {
                Upload upload = transferManager.upload(BUCKET_NAME, objectName, inputStream, metadata);

                upload.waitForCompletion();
            }
            else {
                amazonS3.putObject(BUCKET_NAME, objectName, inputStream, new ObjectMetadata());
            }
        } catch (InterruptedException e) {
            log.error("Download error", e);
            throw new FileLoadException("Download error");
        }
    }

    public String downloadFile(String objectName) throws IOException {
        S3Object s3Object = amazonS3.getObject(BUCKET_NAME, objectName);
        try (InputStream inputStream = s3Object.getObjectContent()) {
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
    }

    public List<String> listFiles() {
        return amazonS3.listObjects(BUCKET_NAME).getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .toList();
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(BUCKET_NAME, fileName);
    }

    public void shutdownBucket() {
        amazonS3.shutdown();
    }
}
