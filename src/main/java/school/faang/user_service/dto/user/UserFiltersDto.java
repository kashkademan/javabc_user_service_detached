package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@NotNull(message = "user filters cannot be null")
public record UserFiltersDto(
        @NotBlank(message = "name pattern cannot be null, empty or a space")
        String namePattern,

        @NotBlank(message = "phone number cannot be null, empty or a space")
        String phoneNumber,

        @PositiveOrZero(message = "minimal experience cannot be less than zero")
        int experienceMin,

        @PositiveOrZero(message = "maximum experience cannot be less than zero")
        int experienceMax
) {
}
