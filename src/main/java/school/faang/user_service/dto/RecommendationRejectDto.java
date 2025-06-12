package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;

public record RecommendationRejectDto(
        @NotNull(message = "Field cannot be null")
        String reason) {
}