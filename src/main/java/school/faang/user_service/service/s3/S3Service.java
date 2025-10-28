package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private final AmazonS3 amazonS3;


    public void saveToFileStorage(MultipartFile multipartFile, String key) {
        long sizeFile = multipartFile.getSize();
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentLength(sizeFile);
        objectMetaData.setContentType(multipartFile.getContentType());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, multipartFile.getInputStream(), objectMetaData
            );
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
