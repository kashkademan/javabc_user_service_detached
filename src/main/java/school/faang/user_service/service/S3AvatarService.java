package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.s3.S3Config;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3AvatarService {
    private final AmazonS3 s3Client;
    private final S3Config s3Config;

    public byte[] downloadImage(String objectKey) {
        var s3Object = s3Client.getObject(s3Config.getBucketName(), objectKey);
        try (var inputStream = s3Object.getObjectContent()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file from S3", e);
        }
    }

    public String getContentType(String objectKey) {
        return s3Client.getObjectMetadata(s3Config.getBucketName(), objectKey).getContentType();
    }

    public String uploadImage(BufferedImage image, String objectName, String format) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, os);
            byte[] bytes = os.toByteArray();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            metadata.setContentType("image/" + format.toLowerCase());

            try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
                s3Client.putObject(s3Config.getBucketName(), objectName, is, metadata);
            }

            return objectName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }

    public void deleteImage(String objectKey) {
        if (objectKey != null) {
            s3Client.deleteObject(s3Config.getBucketName(), objectKey);
        }
    }
}
