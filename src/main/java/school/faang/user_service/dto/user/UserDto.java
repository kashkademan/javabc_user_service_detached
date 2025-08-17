package school.faang.user_service.dto.user;

import lombok.Builder;

import java.util.Locale;

@Builder
public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe,
        PreferredContact preference,
        Locale locale
) {
    public enum PreferredContact {
        EMAIL, PHONE, TELEGRAM
    }
}
