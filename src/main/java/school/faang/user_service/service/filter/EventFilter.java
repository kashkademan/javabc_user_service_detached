package school.faang.user_service.service.filter;

import school.faang.user_service.entity.event.Event;

/**
 * EventFilter — функциональный интерфейс для фильтрации событий.
 * <p>
 * Определяет контракт для фильтра, который принимает объект Event и возвращает true,
 * если событие удовлетворяет определённому условию фильтрации.
 * </p>
 *
 * @author agent
 * @since 04.07.2025
 */
@FunctionalInterface
public interface EventFilter {
    boolean test(Event event);
}