package school.faang.user_service.dto.recommendation;

import lombok.Builder;

@Builder
public record RecommendationEventDto(
        Long authorId,
        Long receiverId,
        Long recommendationId,
        String content
) {
}