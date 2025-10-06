package school.faang.user_service.dto.recommendation;

import java.util.List;

/**
 * DTO для обновления рекомендации.
 * content - новый текст рекомендации
 * skillIds - новый набор подтверждаемых/предлагаемых скиллов (если требуется обновлять)
 */
public record UpdateRecommendationDto(
        String content,
        List<Long> skillIds
) {
}
