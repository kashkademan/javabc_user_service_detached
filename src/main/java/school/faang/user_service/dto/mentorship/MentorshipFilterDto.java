package school.faang.user_service.dto.mentorship;

import school.faang.user_service.entity.RequestStatus;

public record MentorshipFilterDto(
        String description,
        Long requesterId,
        Long receiverId,
        RequestStatus status) {}

