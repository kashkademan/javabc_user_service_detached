package school.faang.user_service.dto.kafka;

import java.util.List;

public record EventStartEventDto(
        Long evenId,
        Long userId,
        List<Long> attendeesIds
) {
}
