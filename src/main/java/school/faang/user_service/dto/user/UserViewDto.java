package school.faang.user_service.dto.user;

import school.faang.user_service.entity.contact.PreferredContact;

public record UserViewDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe,
        PreferredContact preference
) {
}
