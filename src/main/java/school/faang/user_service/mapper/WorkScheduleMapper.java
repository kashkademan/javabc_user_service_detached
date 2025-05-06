package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.WorkSchedule;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    WorkSchedule toWorkSchedule(WorkScheduleDto workScheduleDto);

    WorkScheduleDto toWorkScheduleDto(WorkSchedule workSchedule);
}
