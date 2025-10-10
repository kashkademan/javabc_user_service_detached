package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserDto(
        @Positive
        Long id,

        @NotNull
        String username,
        String email,
        String phone,
        String aboutMe
) {
}
