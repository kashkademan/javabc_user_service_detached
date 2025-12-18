package school.faang.user_service.service.event;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MentorshipRequestedEvent(
        @NotNull
        Long MentorshipRequestSenderId,
        @NotNull
        Long MentorId,
        LocalDateTime timestamp
) {
}
