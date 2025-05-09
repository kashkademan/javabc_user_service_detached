package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.WorkScheduleCreateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    WorkSchedule toWorkSchedule(WorkScheduleCreateDto workScheduleCreateDto);
    WorkScheduleCreateDto toWorkScheduleDto(WorkSchedule workSchedule);
    void update(@MappingTarget WorkSchedule workSchedule, WorkScheduleCreateDto workScheduleCreateDto);
}
