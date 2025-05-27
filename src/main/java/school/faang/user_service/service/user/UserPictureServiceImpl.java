package school.faang.user_service.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.UserPictureService;

import java.util.UUID;

@Service
public class UserPictureServiceImpl implements UserPictureService {

    @Value("${logic.constants.picture_provider_root}")
    private String smallPictureProviderRoot;
    @Value("${logic.constants.default_small_avatar_seed}")
    private String defaultSmallAvatarSeed;

    @Override
    public String getDefaultPictureLink() {
        return smallPictureProviderRoot + '?' + defaultSmallAvatarSeed;
    }

    @Override
    public UserProfilePic generateNewPictureAndReturn() {
        UserProfilePic profilePic = new UserProfilePic();
        String newSeed = UUID.randomUUID().toString();
        profilePic.setSmallFileId(seedValueToPath(newSeed));
        return profilePic;
    }

    private String seedValueToPath(String smallFileId) {
        return smallPictureProviderRoot + "?seed=" + smallFileId;
    }
}
