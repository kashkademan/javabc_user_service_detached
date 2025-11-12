package school.faang.user_service.dto.kafka;

import school.faang.user_service.service.event.scheduling.TimeLeft;

import java.util.List;

public record EventStartEventDto(
        Long eventId,
        Long userId,
        List<Long> attendeesIds,
        String title,
        TimeLeft timeLeft
) {
}
