package school.faang.user_service.service.filter.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.filter.Filter;
import school.faang.user_service.service.filter.FilterService;

import java.util.List;

/**
 * Реализация {@link FilterService} для фильтрации событий {@link Event} на основе параметров {@link EventFilterDto}.
 * <p>
 * Применяет набор фильтров, реализующих интерфейс {@link Filter}, для последовательной фильтрации коллекции событий.
 * Каждый фильтр в списке сначала проверяется на применимость через {@link Filter#isApplicable(Object)},
 * и только затем применяется к потоку сущностей.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class EventFilterServiceImpl implements FilterService<Event, EventFilterDto> {
    private final List<Filter<Event, EventFilterDto>> filters;

    @Override
    public List<Event> getFilteredList(List<Event> entities, EventFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}