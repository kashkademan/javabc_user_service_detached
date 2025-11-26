package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Positive;

public record RecommendationFilterDto(
        String contentContains,
        @Positive
        Long authorId,
        @Positive
        Long receiverId
) {
}
