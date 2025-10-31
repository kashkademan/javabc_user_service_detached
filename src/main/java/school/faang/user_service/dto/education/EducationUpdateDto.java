package school.faang.user_service.dto.education;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record EducationUpdateDto(
        @Min(1980) @Max(2025) Integer yearFrom,
        @Min(1980) @Max(2025) Integer yearTo,
        String institution,
        String educationLevel,
        String specialization) {
}
