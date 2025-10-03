package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;

@Mapper(componentModel = "spring")
public interface UpdateWorkScheduleMapper {
    WorkSchedule toWorkSchedule(UpdateWorkScheduleDto updateWorkScheduleDto);
    UpdateWorkScheduleDto toUpdateWorkScheduleDto(WorkSchedule workSchedule);
}