package school.faang.user_service.dto.user;

import school.faang.user_service.entity.RequestStatus;
import java.util.Date;

public record RecommendationRequestDto(Long id, String message, UserDto requester, UserDto receiver,
                                       RequestStatus status, Date created_ad, Date updated_at) {
}
