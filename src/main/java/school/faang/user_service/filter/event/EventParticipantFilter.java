package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class EventParticipantFilter implements Filter<Event, EventFilterDto> {

    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.participantId() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events
                .filter(event -> event.getAttendees().parallelStream()
                        .anyMatch(user -> user.getId().equals(eventFilterDto.participantId())));
    }
}
