package school.faang.user_service.service.filter;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

/**
 * Интерфейс фильтра для событий.
 * <p>
 * Используется для фильтрации списка событий по заданным параметрам.
 * </p>
 */
public interface EventFilter {

    /**
     * Проверяет, нужно ли применять этот фильтр для заданных параметров.
     *
     * @param dto параметры фильтрации
     * @return true, если фильтр должен быть применён
     */
    boolean isApplicable(EventFilterDto dto);

    /**
     * Применяет фильтр к потоку событий.
     *
     * @param events поток событий
     * @param dto    параметры фильтрации
     * @return отфильтрованный поток событий
     */
    Stream<Event> filter(Stream<Event> events, EventFilterDto dto);
}