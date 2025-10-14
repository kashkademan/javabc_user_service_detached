package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestByIdDto(
        @NotNull
        @Positive
        Long id) {
}