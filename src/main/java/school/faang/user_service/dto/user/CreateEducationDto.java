package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEducationDto(
        @NotNull @Min(1980) @Max(2025) Integer yearFrom,
        @NotNull @Min(1980) @Max(2025) Integer yearTo,
        @NotBlank String institution,
        @NotBlank String educationLevel,
        @NotBlank String specialization) {
}
