package school.faang.user_service.dto.career;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CareerCreateDto(
        @NotNull LocalDate from,
        @NotNull LocalDate to,
        @NotNull String company,
        @NotNull String position) {
}
