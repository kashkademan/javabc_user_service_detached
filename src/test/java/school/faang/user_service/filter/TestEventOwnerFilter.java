package school.faang.user_service.filter;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public class TestEventOwnerFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return true;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events.filter(event ->
                event.getOwner() != null && event.getOwner().getId().equals(1L));
    }
}
