package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe
) {
}
