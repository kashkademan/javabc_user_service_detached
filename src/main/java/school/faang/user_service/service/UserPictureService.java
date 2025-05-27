package school.faang.user_service.service;

import school.faang.user_service.entity.UserProfilePic;

public interface UserPictureService {
    String getDefaultPictureSeed();
    UserProfilePic generateNewPictureAndReturn();
}
