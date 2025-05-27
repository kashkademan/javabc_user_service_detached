package school.faang.user_service.service;

public interface UserPictureService {
    String getDefaultPictureSeed();
    String generateNewSeedSaveAndReturn(Long userId);
}
