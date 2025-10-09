package school.faang.user_service.controller.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.workschedule.CreateWorkScheduleDto;
import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkScheduleFacade {
    private final WorkScheduleService workScheduleService;
    private final WorkScheduleMapper workScheduleMapper;

    public WorkScheduleDto addWorkSchedule(CreateWorkScheduleDto createWorkScheduleDto) {
        WorkSchedule workSchedule = workScheduleMapper.toCreateWorkSchedule(createWorkScheduleDto);
        WorkSchedule savedWorkSchedule = workScheduleService.addWorkSchedule(workSchedule);
        log.info("WorkSchedule for user added");

        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    public WorkScheduleDto updateWorkSchedule(long workScheduleId, UpdateWorkScheduleDto updateWorkScheduleDto) {
        WorkSchedule workSchedule = workScheduleService.updateWorkSchedule(workScheduleId, updateWorkScheduleDto);
        log.info("WorkSchedule was updated");

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule workSchedule = workScheduleService.getById(workScheduleId);

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }
}
