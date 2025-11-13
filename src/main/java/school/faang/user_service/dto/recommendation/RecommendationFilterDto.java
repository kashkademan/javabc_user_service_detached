package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;

public record RecommendationFilterDto(
        String contentContains,
        @NotNull(message = "Recommendation author ID cannot be absent")
        Long authorId,
        @NotNull(message = "Recommendation received ID cannot be absent")
        Long receiverId
) {
}