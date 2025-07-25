package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateDto(
        @NotBlank
        String username,
        @NotBlank
        String email,
        @NotBlank
        String password,
        @NotNull
        Long countryId
) {
}
