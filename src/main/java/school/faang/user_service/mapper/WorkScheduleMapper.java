package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleViewDto;
import school.faang.user_service.entity.user.WorkSchedule;

/**
 * WorkScheduleMapper — для преобразования между сущностью {@link WorkSchedule} и DTO.
 * <p>
 * Представляет методы для конвертации данных.
 * </p>
 *
 * @author agent
 * @since 07.07.2025
 */
@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {

    /**
     * Преобразует DTO создания расписания в сущность {@link WorkSchedule}.
     */
    WorkSchedule toWorkSchedule(WorkScheduleCreateDto workScheduleCreateDto);

    /**
     * Преобразует сущность {@link WorkSchedule} в DTO для отображения.
     */
    WorkScheduleViewDto toWorkScheduleDto(WorkSchedule workSchedule);

    /**
     * Обновляет сущность {@link WorkSchedule} в DTO для отображения.
     */
    void updateWorkScheduleFromDto(WorkScheduleUpdateDto dto, @MappingTarget WorkSchedule workSchedule);

}