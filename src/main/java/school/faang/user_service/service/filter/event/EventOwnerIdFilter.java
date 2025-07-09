package school.faang.user_service.service.filter.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

/**
 * EventOwnerIdFilter — фильтр для проверки принадлежности события определённому владельцу.
 * <p>
 * Возвращает true, если id владельца события совпадает с указанным id.
 * Если id владельца равен null, фильтр всегда пропускает событие.
 * </p>
 *
 * @author agent
 * @since 04.07.2025
 */
@Component
@RequiredArgsConstructor
public class EventOwnerIdFilter implements Filter<Event, EventFilterDto> {

    @Override
    public boolean isApplicable(EventFilterDto dto) {
        return dto.getOwnerId() != null;
    }

    @Override
    public Stream<Event> filter(Stream<Event> events, EventFilterDto dto) {
        Long ownerId = dto.getOwnerId();

        return events.filter(event ->
                event.getOwner() != null
                        && event.getOwner().getId() != null
                        && event.getOwner().getId().equals(ownerId)
        );
    }
}