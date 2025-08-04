package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserPictureService {
    String getDefaultPictureLink();

    String generateNewSmallPicture();

    void uploadAvatar(long userId, MultipartFile file);

    byte[] getAvatar(long userId, String sizeMarker);

    void deleteAvatar(long userId);
}
