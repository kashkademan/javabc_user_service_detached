package school.faang.user_service.service.profile;

import school.faang.user_service.dto.user.UserDto;

public interface ProfileService {
    UserDto getProfile(long userId);
}
