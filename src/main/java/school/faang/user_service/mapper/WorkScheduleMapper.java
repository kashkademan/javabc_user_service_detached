package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.WorkSchedule;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {

    @Mapping(target = "user", ignore = true)
    WorkSchedule toWorkScheduleEntity(WorkScheduleDto workScheduleDto);

    WorkScheduleDto toWorkScheduleDto(WorkSchedule workSchedule);
}
