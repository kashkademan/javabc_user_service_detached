package school.faang.user_service.dto.recommendation;

import java.time.LocalDateTime;

public record RecommendationEventDto(
        Long recommendationId,
        Long authorId,
        Long receiverId,
        LocalDateTime createdAt) {
}
