package school.faang.user_service.dto.recommendation;

/**
 * Класс-событие для запроса рекомендации
 *
 * @param requesterId id отправителя
 * @param receiverId id получателя
 * @param requestId id запроса рекомендации
 *
 * @author Linempy
 * @since 13.08.2025
 */
public record RecommendationRequestedEvent(
        Long requesterId,
        Long receiverId,
        Long requestId
) {
}