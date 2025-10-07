package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    WorkSchedule toWorkSchedule(WorkScheduleDto workScheduleDto);

    void updateWorkSchedule(UpdateWorkScheduleDto updateWorkScheduleDto, @MappingTarget WorkSchedule entity);

    WorkScheduleDto toWorkScheduleDto(WorkSchedule workSchedule);
}
