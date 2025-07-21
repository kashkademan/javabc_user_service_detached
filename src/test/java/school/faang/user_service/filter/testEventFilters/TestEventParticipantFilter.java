package school.faang.user_service.filter.testEventFilters;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.Objects;
import java.util.stream.Stream;

public class TestEventParticipantFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return true;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events
                .filter(event ->event.getAttendees()
                        .stream()
                        .anyMatch(u -> Objects.equals(u.getId(), 4L)));
    }
}
