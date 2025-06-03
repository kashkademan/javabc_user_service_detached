package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.RandomAvatarConfiguration;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.UserPictureService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPictureServiceImpl implements UserPictureService {

    private final RandomAvatarConfiguration config;

    @Override
    public String getDefaultPictureLink() {
        return config.getPictureProviderRootUrl() + '?' + config.getDefaultSmallAvatarSeed();
    }

    @Override
    public UserProfilePic generateNewPicture() {
        UserProfilePic profilePic = new UserProfilePic();
        String newSeed = UUID.randomUUID().toString();
        profilePic.setSmallFileId(seedValueToPath(newSeed));
        return profilePic;
    }

    private String seedValueToPath(String smallFileId) {
        return config.getPictureProviderRootUrl() + "?seed=" + smallFileId;
    }
}
