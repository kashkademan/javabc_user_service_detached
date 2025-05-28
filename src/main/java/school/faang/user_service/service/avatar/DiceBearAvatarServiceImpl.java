package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.avatar.DiceBearConfig;
import school.faang.user_service.entity.User;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiceBearAvatarServiceImpl implements AvatarService {

    private final DiceBearConfig diceBearConfig;
    private static final String DEFAULT_SEED = "defaultUserSeed";

    @Override
    public String generateAvatarUrl(User user) {
        String seedSource;
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            seedSource = DEFAULT_SEED;
        } else {
            seedSource = user.getUsername();
        }
        String encodedSeed = URLEncoder.encode(seedSource, StandardCharsets.UTF_8);
        String apiUrl = diceBearConfig.getApiUrl();
        String style = diceBearConfig.getStyle();

        return String.format("%s/%s/svg?seed=%s", apiUrl, style, encodedSeed);
    }
}