package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class EventParticipantFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.participantId() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        Long participantId = filter.participantId();

        return events.filter(event ->
                event.getAttendees()
                        .stream()
                        .anyMatch(u -> Objects.equals(u.getId(), participantId)));
    }
}
