package school.faang.user_service.dto.user;

import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe,
        PreferredContact contactPreference,
        List<ContactDto> contacts
) {
}
