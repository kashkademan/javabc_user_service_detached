package school.faang.user_service.dto.career;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateCareerDto(
        @NotBlank(message = "From date should be present!")
        LocalDate from,
        LocalDate to,
        @NotBlank(message = "Company should be present!")
        String company,
        @NotBlank(message = "Position should be present!")
        String position
) {
}
