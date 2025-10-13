package school.faang.user_service.dto.career;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateCareerDto(@NotNull LocalDate from,
                              LocalDate to,
                              @NotBlank String company,
                              @NotBlank String position)
        implements CareerDateDto {
}