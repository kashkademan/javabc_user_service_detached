package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import lombok.With;
import school.faang.user_service.entity.RequestStatus;

@With
@Builder
public record MentorshipRequestFilterDto(
        Long requesterId,
        Long receiverId,
        RequestStatus status
) {
}