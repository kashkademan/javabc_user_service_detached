package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.entity.UserProfilePic;

public interface UserPictureService {
    String getDefaultPictureLink();
    UserProfilePic generateNewPicture();
    UserPersonalDto uploadAvatar(long userId, MultipartFile file);
    byte[] getAvatar(long userId, String sizeMarker);
    void deleteAvatar(long userId);
}
