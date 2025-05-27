package school.faang.user_service.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.UserPictureService;

@Service
public class UserPictureServiceImpl implements UserPictureService {


    @Value("${logic.constants.picture_provider_root}")
    private String smallPictureProviderRoot;
    @Value("${logic.constants.default_small_avatar_seed}")
    private String defaultSmallAvatarSeed;

    @Override
    public String getDefaultPictureSeed() {
        return smallPictureProviderRoot + '?' + defaultSmallAvatarSeed;
    }

    @Override
    public String generateNewSeedSaveAndReturn(Long userId) {
        return null;
    }
}
