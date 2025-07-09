package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleViewDto;
import school.faang.user_service.entity.user.User;
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
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkScheduleMapper {

    /**
     * Преобразует DTO создания расписания в сущность {@link WorkSchedule}.
     */
    WorkSchedule toEntity(WorkScheduleCreateDto workScheduleCreateDto, User user);

    /**
     * Преобразует сущность {@link WorkSchedule} в DTO для отображения.
     */
    WorkScheduleViewDto toViewDto(WorkSchedule workSchedule);

    /**
     * Обновляет сущность {@link WorkSchedule} в DTO для отображения.
     */
    void update(WorkScheduleUpdateDto dto, @MappingTarget WorkSchedule workSchedule);

}