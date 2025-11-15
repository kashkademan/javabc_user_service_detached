package school.faang.user_service.dto.kafka;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.scheduling.TimeLeft;

import java.util.List;

public record EventStartEventDto(
        Long eventId,
        Long userId,
        String nameOwner,
        List<UserDto> attendeesUser,
        String titleEvent,
        TimeLeft timeLeft
) {
}
