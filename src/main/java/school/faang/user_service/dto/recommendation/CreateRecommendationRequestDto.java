package school.faang.user_service.dto.recommendation;

import java.time.LocalDateTime;
import java.util.List;

public record CreateRecommendationRequestDto(
        Long receiverId,
        String message,
        List<Long> skillIds,
        LocalDateTime createdAt
) {
}
