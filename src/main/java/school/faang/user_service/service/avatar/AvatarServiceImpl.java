package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.DiceBearConfig;
import school.faang.user_service.entity.User;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private final DiceBearConfig diceBearConfig;

    @Override
    public String generateAvatarUrl(User user) {
        String seedSource;
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            seedSource = user.getUsername();
        } else if (user.getId() != null) {
            seedSource = String.valueOf(user.getId());
        } else {
            seedSource = user.getEmail() != null ? user.getEmail() : "defaultUserSeed";
        }

        return String.format("%s/%s/svg?seed=%s", diceBearConfig.getApiUrl(), diceBearConfig.getStyle(), seedSource);
    }
}




