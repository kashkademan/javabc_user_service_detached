package school.faang.user_service.dto.user;

import jakarta.annotation.Nullable;
import school.faang.user_service.entity.contact.PreferredContact;

public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe,
        @Nullable PreferredContact preferredContact
) {
    public UserDto(Long id, String username, String email, String phone, String aboutMe) {
        this(id, username, email, phone, aboutMe, null);
    }
}
