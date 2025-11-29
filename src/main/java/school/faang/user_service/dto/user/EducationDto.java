package school.faang.user_service.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EducationDto(
        long id,
        @NotNull(message = "Year from should be present!")
        Integer yearFrom,
        Integer yearTo,
        @NotBlank(message = "Institution should be present!")
        String institution,
        String educationLevel,
        String specialization
) {
}