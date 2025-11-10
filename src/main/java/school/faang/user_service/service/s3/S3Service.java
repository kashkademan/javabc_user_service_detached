package school.faang.user_service.service.s3;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.resource.Resource;

public interface S3Service {
    Resource uploadFile(MultipartFile file, String folder);
}