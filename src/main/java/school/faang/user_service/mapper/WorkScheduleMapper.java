package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    @Mapping(target = "user", ignore = true)
    WorkSchedule toWorkSchedule(WorkScheduleDto workScheduleDto);

    WorkScheduleDto toWorkScheduleDto(WorkSchedule workSchedule);
}
