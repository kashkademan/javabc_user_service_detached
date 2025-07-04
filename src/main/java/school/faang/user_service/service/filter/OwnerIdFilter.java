package school.faang.user_service.service.filter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.event.Event;

/**
 * OwnerIdFilter — фильтр для проверки принадлежности события определённому владельцу.
 * <p>
 * Возвращает true, если id владельца события совпадает с указанным id.
 * Если id владельца равен null, фильтр всегда пропускает событие.
 * </p>
 *
 * @author agent
 * @since 04.07.2025
 */
@RequiredArgsConstructor
public class OwnerIdFilter implements EventFilter {
    private final Long ownerId;

    @Override
    public boolean test(Event event) {
        return ownerId == null || event.getOwner().getId().equals(ownerId);
    }
}