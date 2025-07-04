package school.faang.user_service.service.filter;

import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;

/**
 * EventFilterFactory — фабрика для создания списка фильтров на основе DTO с критериями фильтрации.
 * <p>
 * Используется для конвертации параметров фильтрации, полученных извне, в конкретные объекты фильтров,
 * которые можно применять к событиям.
 * </p>
 * <p>
 * Все фильтры, создаваемые фабрикой, могут использоваться совместно для составной фильтрации.
 * </p>
 *
 * @author agent
 * @since 04.07.2025
 */
public class EventFilterFactory {

    public static List<EventFilter> buildFilters(EventFilterDto dto) {
        return List.of(
                new TitleContainsFilter(dto.getTitleContains()),
                new DescriptionContainsFilter(dto.getDescriptionContains()),
                new OwnerIdFilter(dto.getOwnerId()),
                new ParticipantIdFilter(dto.getParticipantId()),
                new TypeFilter(dto.getType())
        );
    }
}