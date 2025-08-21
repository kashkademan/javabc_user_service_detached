package school.faang.user_service.filter;

import org.apache.commons.lang3.StringUtils;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public class TestEventDescriptionFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return true;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events.filter(e ->
                StringUtils.containsIgnoreCase(e.getDescription(), "description"));
    }
}
