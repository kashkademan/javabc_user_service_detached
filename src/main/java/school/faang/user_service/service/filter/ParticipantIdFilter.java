package school.faang.user_service.service.filter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.event.Event;

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
@RequiredArgsConstructor
public class ParticipantIdFilter implements EventFilter {
    private final Long participantId;


    @Override
    public boolean test(Event event) {
        return participantId == null || event.getAttendees().stream()
                .anyMatch(attendee -> attendee.getId().equals(participantId));
    }
}