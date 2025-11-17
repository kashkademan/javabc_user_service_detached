package school.faang.user_service.dto.user;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.Locale;

@FieldNameConstants
@Builder
public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe,
        Locale locale,
        PreferredContact preference
) {
}
