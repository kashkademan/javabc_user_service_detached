package school.faang.user_service.amazon_s3;

import com.amazonaws.auth.policy.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {
    String uploadFile(long userId, MultipartFile file, String folder);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}
