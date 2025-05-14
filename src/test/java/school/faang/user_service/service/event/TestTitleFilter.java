package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.filter.EventFilter;

import java.util.stream.Stream;

public class TestTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return true;
    }

    @Override
    public Stream<EventDto> apply(Stream<EventDto> eventStream, EventFilterDto filter) {
        return eventStream.filter((eventDto) -> eventDto.getTitle().equals(EventServiceImplTest.TITLE_FOR_FILTER));
    }
}
