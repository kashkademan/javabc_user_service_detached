package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.work_schedule_dto.WorkScheduleCreateDto;
import school.faang.user_service.dto.work_schedule_dto.WorkScheduleDto;
import school.faang.user_service.dto.work_schedule_dto.WorkScheduleUpdateDto;
import school.faang.user_service.entity.WorkSchedule;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    WorkSchedule toWorkSchedule(WorkScheduleCreateDto workScheduleCreateDto);
    WorkScheduleDto toWorkScheduleDto(WorkSchedule workSchedule);
    WorkSchedule toWorkScheduleUpdate(WorkScheduleUpdateDto workScheduleCreateDto);
    WorkScheduleUpdateDto toWorkScheduleUpdateDto(WorkSchedule workSchedule);
}
