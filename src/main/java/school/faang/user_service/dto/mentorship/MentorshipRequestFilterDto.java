package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.RequestStatus;

public record MentorshipRequestFilterDto(
        @NotNull(message = "RequesterID не может быть null")
        Long requesterId,
        @NotNull(message = "ReceiverID не может быть null")
        Long receiverId,
        RequestStatus status
) {
}
