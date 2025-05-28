package school.faang.user_service.service.avatar;

import school.faang.user_service.entity.User;

public interface AvatarService {
    String generateAvatarUrl(User user);
}