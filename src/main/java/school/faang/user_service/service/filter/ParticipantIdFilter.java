package school.faang.user_service.service.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

/**
 * ParticipantIdFilter — фильтр для проверки участия пользователя в событии.
 * <p>
 * Возвращает true, если среди участников события есть пользователь с указанным id.
 * Если id участника равен null, фильтр всегда пропускает событие.
 * </p>
 *
 * @author agent
 * @since 04.07.2025
 */
@Component
@RequiredArgsConstructor
public class ParticipantIdFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto dto) {
        return dto.getParticipantId() != null;
    }

    @Override
    public Stream<Event> filter(Stream<Event> events, EventFilterDto dto) {
        long participantId = dto.getParticipantId();

        return events.filter(event ->
                event.getAttendees() != null
                && event.getAttendees().stream()
                        .anyMatch(user -> user.getId() != null
                                          && user.getId().equals(participantId))
        );
    }
}