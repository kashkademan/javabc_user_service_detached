package school.faang.user_service.dto.recommendation;

import java.util.List;

/**
 * DTO для создания рекомендации.
 * skillIds - необязательный список подтверждаемых/предлагаемых скиллов.
 */
public record CreateRecommendationDto(
        Long receiverId,
        String content,
        List<Long> skillIds
) {
}
