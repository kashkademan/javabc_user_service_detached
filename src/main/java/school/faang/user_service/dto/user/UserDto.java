package school.faang.user_service.dto.user;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Builder
public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe
) {
}
