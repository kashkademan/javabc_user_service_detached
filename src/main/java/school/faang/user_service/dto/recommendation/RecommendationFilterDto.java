package school.faang.user_service.dto.recommendation;

/**
 * Фильтры для поиска рекомендаций.
 */
public record RecommendationFilterDto(
        String contentContains,
        Long authorId,
        Long receiverId
) {
}
