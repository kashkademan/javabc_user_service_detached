package school.faang.user_service.service.s3;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.resource.Resource;

import java.io.IOException;

public interface S3Service {
    Resource uploadFile(MultipartFile file, String folder);

    Resource uploadFile(byte[] fileData, String filename, String contentType, String folder);

    void deleteFile(String fileKey);

    MultipartFile getFile(String key) throws IOException;
}