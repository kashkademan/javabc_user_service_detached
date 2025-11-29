package school.faang.user_service.event.mentorship;

import lombok.Builder;

@Builder
public record MentorshipAcceptedEvent(
        Long mentorshipRequestId,
        Long mentorId,
        Long menteeId
) {
}