package school.faang.user_service.filter;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.stream.Stream;

public interface EventFilter {
    boolean isApplicable(EventFilterDto filter);

    Stream<EventDto> apply(Stream<EventDto> eventStream, EventFilterDto filter);
}
