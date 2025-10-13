package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkScheduleMapper {
    WorkScheduleDto toWorkScheduleDto(WorkSchedule workSchedule);
}
