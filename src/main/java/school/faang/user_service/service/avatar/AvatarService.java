package school.faang.user_service.service.avatar;

import school.faang.user_service.dto.avatar.AvatarDto;

public interface AvatarService {

    String getAvatarUrl(Long userId);

    AvatarDto getAvatar(Long userId);

    AvatarDto generateAvatar(Long userId);
}
