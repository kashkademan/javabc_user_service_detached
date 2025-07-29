package school.faang.user_service.dto.recommendation;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationRequestViewDto(
        Long id,
        String message,
        UserDto requester,
        UserDto receiver,
        RequestStatus status,
        List<Long> skillIds,
        LocalDateTime createdAt
) {
}
