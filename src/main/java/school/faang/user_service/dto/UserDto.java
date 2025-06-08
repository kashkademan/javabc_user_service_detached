package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
        Long id,

        @NotBlank(message = "Field cannot be blank")
        String username,

        @NotBlank(message = "Field cannot be blank")
        @Email(message = "Field must be an email")
        String email
) {}
