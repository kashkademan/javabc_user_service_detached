package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDto(
        @NotBlank
        String username,
        @NotBlank
        String email,
        String phone,
        String aboutMe,
        @NotNull
        Long countryId,
        String city
) {
}
