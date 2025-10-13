package school.faang.user_service.dto.career;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateCareerDto(@NotNull LocalDate from,
                              LocalDate to,
                              @NotBlank String company,
                              @NotBlank String position) implements CareerDateDto {
}