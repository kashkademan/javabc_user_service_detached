package school.faang.user_service.dto.user;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe
) {
}
