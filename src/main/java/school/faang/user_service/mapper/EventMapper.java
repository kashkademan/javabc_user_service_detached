package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.event.Event;

/**
 * Mapper для преобразования между сущностью {@link Event} и DTO.
 * <p>
 * Предоставляет методы для конвертации данных при создании, обновлении и отображении событий.
 * </p>
 *
 * @author JekaCAP
 */
@Mapper(componentModel = "spring")
public interface EventMapper {

    /**
     * Преобразует DTO создания события в сущность {@link Event}.
     */
    Event toEntity(EventCreateDto eventDto);

    /**
     * Преобразует сущность {@link Event} в DTO для отображения.
     */
    @Mapping(target = "ownerId", source = "owner.id")
    EventViewDto toViewDto(Event event);

    /**
     * Обновляет сущность {@link Event} на основе данных из DTO обновления.
     */
    void update(EventUpdateDto eventDto, @MappingTarget Event event);
}