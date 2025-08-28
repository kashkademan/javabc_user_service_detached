package school.faang.user_service.dto.recommendation;

import java.time.LocalDateTime;

/**
 * Класс-ивент рекомендации для сервиса аналитики
 *
 * @author Linempy
 * @since 20.08.2025
 */
public record RecommendationEvent(
        Long requesterId,
        Long receiverId,
        Long recommendationId,
        LocalDateTime receivedAt
) {
}