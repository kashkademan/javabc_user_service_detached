package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RequestFilterDto(
        @NotNull(message = "Field cannot be null")
        Long requesterId,

        @NotNull(message = "Field cannot be null")
        Long receiverId,

        Long recommendationId,

        @NotNull(message = "Field cannot be null")
        @NotBlank(message = "Field cannot be blank")
        String messagePattern,

        LocalDateTime createdAfter,

        LocalDateTime createdBefore
) {
}