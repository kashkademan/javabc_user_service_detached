package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

@Component
public class EventTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.titleContains() != null && !eventFilterDto.titleContains().isBlank();
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events
                .filter(e -> containsIgnoreCase(e.getTitle(), eventFilterDto.titleContains()));
    }
}
