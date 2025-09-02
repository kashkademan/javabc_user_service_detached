package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record UserCreateDto(
        @NotBlank
        String username,
        @NotBlank
        String email,
        @NotBlank
        String password,
        @NonNull
        Long countryId
) {
}
