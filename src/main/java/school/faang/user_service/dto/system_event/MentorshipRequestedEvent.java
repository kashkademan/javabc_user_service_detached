package school.faang.user_service.dto.system_event;

import java.time.LocalDateTime;

public record MentorshipRequestedEvent(Long requesterId, Long receiverId, LocalDateTime createdAt) {
}
