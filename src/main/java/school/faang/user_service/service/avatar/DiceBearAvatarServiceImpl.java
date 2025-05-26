package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.avatar.DiceBearConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiceBearAvatarServiceImpl implements DiceBearAvatarService {

    private final DiceBearConfig diceBearConfig;
    private static final String DEFAULT_SEED = "defaultUserSeed";

    @Override
    public String generateAvatarUrl(User user) {
        String seedSource = (user.getUsername() != null && !user.getUsername().isEmpty())
                ? user.getUsername() : DEFAULT_SEED;

        String encodedSeed = URLEncoder.encode(seedSource, StandardCharsets.UTF_8);

        String apiUrl = diceBearConfig.getApiUrl();
        String style = diceBearConfig.getStyle();

        if (style == null || style.isEmpty()) {
            style = "pixelated";
            log.warn("DiceBearConfig.style is missing; using default: {}", style);
        }
        if (apiUrl == null || apiUrl.isEmpty()) {
            apiUrl = "https://api.dicebear.com/9.x";
            log.warn("DiceBearConfig.apiUrl is missing; using default: {}", apiUrl);
        }

        String avatarUrl =  String.format("%s/%s/svg?seed=%s", apiUrl, style, encodedSeed);

        if (user.getUserProfilePic()==null)  {
            user.setUserProfilePic(new UserProfilePic());
        }
        user.getUserProfilePic().setSmallFileId(avatarUrl);
        return avatarUrl;
    }
}