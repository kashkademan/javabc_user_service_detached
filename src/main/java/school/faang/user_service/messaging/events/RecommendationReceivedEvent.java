package school.faang.user_service.messaging.events;

import java.time.LocalDateTime;

public record RecommendationReceivedEvent(
        Long recommendationId,
        Long authorId,
        Long receiverId,
        LocalDateTime createdAt) {
}
