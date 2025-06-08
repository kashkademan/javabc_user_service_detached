package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Builder
public record RequestFilterDto(
        @NotNull(message = "Field cannot be null")
        Long requesterId,

        @NotNull(message = "Field cannot be null")
        Long receiverId,

        Long recommendationId,

        @NotBlank(message = "Field cannot be blank")
        @Size(max = 255, message = "Field length must be less or equal 255")
        String messagePattern,

        LocalDateTime createdAfter,

        LocalDateTime createdBefore
) {
}