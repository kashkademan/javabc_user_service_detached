package school.faang.user_service.service.avatar;

import school.faang.user_service.entity.User;

public interface DiceBearAvatarService {
    String generateAvatarUrl(User user);
}