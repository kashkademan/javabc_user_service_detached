package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.filter.EventFilter;

import java.util.stream.Stream;

@Component
public class LocationFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getLocation() != null;
    }

    @Override
    public Stream<EventDto> apply(Stream<EventDto> eventStream, EventFilterDto filter) {
        return eventStream.filter(event -> event.getLocation().equals(filter.getLocation()));
    }
}
