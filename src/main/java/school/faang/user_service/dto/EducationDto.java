package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;

public record EducationDto(
        @Min(value = 1, message = "id must be a positive number")
        long id,

        Integer yearFrom,
        Integer yearTo,
        String institution,
        String educationLevel,
        String specialization) {
}
