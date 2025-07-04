package school.faang.user_service.service.filter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

/**
 * TypeFilter — фильтр по типу события.
 * <p>
 * Возвращает true, если тип события совпадает с указанным.
 * Если тип равен null, фильтр всегда пропускает событие.
 * </p>
 *
 * @author agent
 * @since 04.07.2025
 */
@RequiredArgsConstructor
public class TypeFilter implements EventFilter {
    private final EventType type;


    @Override
    public boolean test(Event event) {
        return type == null || event.getType() == type;
    }
}