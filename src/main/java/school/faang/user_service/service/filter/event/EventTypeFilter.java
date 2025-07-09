package school.faang.user_service.service.filter.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

/**
 * EventTypeFilter — фильтр по типу события.
 * <p>
 * Возвращает true, если тип события совпадает с указанным.
 * Если тип равен null, фильтр всегда пропускает событие.
 * </p>
 *
 * @author agent
 * @since 04.07.2025
 */
@Component
@RequiredArgsConstructor
public class EventTypeFilter implements Filter<Event, EventFilterDto> {

    @Override
    public boolean isApplicable(EventFilterDto dto) {
        return dto.getType() != null;
    }

    @Override
    public Stream<Event> filter(Stream<Event> events, EventFilterDto dto) {
        EventType type = dto.getType();
        return events.filter(event -> event.getType() == type);
    }
}