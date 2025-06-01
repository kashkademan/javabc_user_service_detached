package school.faang.user_service.service.avatar;

import school.faang.user_service.dto.avatar.AvatarDto;
import school.faang.user_service.entity.User;

public interface AvatarService {
    String generateAvatarUrl(User user);

    String getAvatarUrl(Long userId);

    AvatarDto getAvatar(Long userId);

    AvatarDto generateAvatar(Long userId);
}
