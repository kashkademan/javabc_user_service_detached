package school.faang.user_service.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import school.faang.user_service.config.context.MinioStorage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class MinioService {
    private final MinioStorage storage;
    private static final String END_POINT = "http://localhost:9000";
    private static final String ACCESS_KEY = "user";
    private static final String SECRET_KEY = "password";
    private static final String REGION = "us-east-1";
    private final String BUCKET_NAME;

    public MinioService(String bucketName) {
        this.storage = new MinioStorage(END_POINT, ACCESS_KEY, SECRET_KEY, REGION);
        this.BUCKET_NAME = bucketName;
    }

    public void createBucket() {
        storage.createBucket(BUCKET_NAME);
    }

    public void uploadFile(String object, String objectName) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(object.getBytes())) {
            storage.uploadFile(BUCKET_NAME, objectName, inputStream);
        }
    }

    public String downloadFile(String objectName) throws IOException {
        S3Object s3Object = storage.downloadFile(BUCKET_NAME, objectName);
        try (InputStream inputStream = s3Object.getObjectContent()) {
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
    }

    public List<String> listFiles() {
        return storage.listFiles(BUCKET_NAME).stream()
                .map(S3ObjectSummary::getKey)
                .toList();
    }

    public void deleteFile(String fileName) {
        storage.deleteFile(BUCKET_NAME, fileName);
    }

    public void shutdownBucket(){
        storage.shutdown();
    }
}
