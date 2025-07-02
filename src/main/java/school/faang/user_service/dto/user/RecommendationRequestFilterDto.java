package school.faang.user_service.dto.user;

import school.faang.user_service.entity.RequestStatus;

public record RecommendationRequestFilterDto(Long requesterId, Long receiverId,
                                             String messageContains, RequestStatus status) {
}
