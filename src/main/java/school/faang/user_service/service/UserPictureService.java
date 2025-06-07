package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserPersonalDto;

public interface UserPictureService {
    String getDefaultPictureLink();
    String generateNewSmallPicture();
    UserPersonalDto uploadAvatar(long userId, MultipartFile file);
    byte[] getAvatar(long userId, String sizeMarker);
    void deleteAvatar(long userId);
}
