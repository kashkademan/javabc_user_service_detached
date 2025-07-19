package school.faang.user_service.service.user;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.response.UploadAvatarResponseDto;

public interface UserAvatarService {

    UploadAvatarResponseDto uploadAvatar(Long userId, MultipartFile file);

    InputStreamResource downloadLargeAvatar(Long userId);

    InputStreamResource downloadSmallAvatar(Long userId);

    void deleteAvatar(Long userId);
}
