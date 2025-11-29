package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Positive;
import school.faang.user_service.entity.RequestStatus;

public record RecommendationRequestFilterDto(
        @Positive
        Long requesterId,
        @Positive
        Long receiverId,
        String messageContains,
        RequestStatus status
) {
}
