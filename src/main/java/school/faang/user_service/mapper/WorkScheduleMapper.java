package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.entity.user.WorkSchedule;

import java.util.function.Consumer;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkScheduleMapper {
    WorkScheduleDto toWorkScheduleDto(WorkSchedule workSchedule);

    static WorkSchedule createScheduleFields(WorkScheduleCreateDto dto) {
        return WorkSchedule.builder()
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .startLunch(dto.startLunch())
                .endLunch(dto.endLunch())
                .timezone(dto.timezone())
                .build();
    }

    static void updateScheduleFields(WorkSchedule entity, WorkScheduleUpdateDto dto) {
        updateIfNotNull(dto.startTime(), entity::setStartTime);
        updateIfNotNull(dto.endTime(), entity::setEndTime);
        updateIfNotNull(dto.startLunch(), entity::setStartLunch);
        updateIfNotNull(dto.endLunch(), entity::setEndLunch);
        updateIfNotNull(dto.timezone(), entity::setTimezone);
    }

    static  <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
