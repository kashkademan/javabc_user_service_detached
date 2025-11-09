package school.faang.user_service.dto.kafka;

import java.util.List;

public record EventStartEventDto(
        Long eventId,
        Long userId,
        List<Long> attendeesIds
) {
}
