package school.faang.user_service.dto.education;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEducationDto(
        @NotNull
        Integer yearFrom,
        Integer yearTo,
        @NotNull
        @NotBlank
        String institution,
        String educationLevel,
        String specialization
) {
}
