package school.faang.user_service.dto.user;

import jakarta.validation.constraints.*;
import lombok.Builder;


@Builder
public record CreateUserDto(
        @NotBlank
        @Size(min = 1, max = 50)
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Username may contain letters, digits, dot, underscore, hyphen")
        String username,

        @NotBlank @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 200)
        String password,

        @NotNull @Positive
        Long countryId
) {}