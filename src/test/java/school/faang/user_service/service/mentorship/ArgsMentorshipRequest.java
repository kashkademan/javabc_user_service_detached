package school.faang.user_service.service.mentorship;

import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public record ArgsMentorshipRequest(
        RequestStatus status,
        long menteeId,
        long mentorId,
        LocalDateTime createdAt
) {

}