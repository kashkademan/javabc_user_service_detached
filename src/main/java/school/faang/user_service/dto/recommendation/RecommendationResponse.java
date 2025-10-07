package school.faang.user_service.dto.recommendation;

/**
 * DTO для отображения рекомендации.
 * При необходимости можно дополнить датами создания/обновления и др. полями.
 */
public record RecommendationResponse(
        Long id,
        Long authorId,
        Long receiverId,
        String content
) {
}
