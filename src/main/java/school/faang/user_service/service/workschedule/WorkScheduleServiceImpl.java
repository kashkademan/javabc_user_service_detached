package school.faang.user_service.service.workschedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

@Slf4j
@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private UserRepository userRepository;
    private WorkScheduleRepository workScheduleRepository;
    private WorkScheduleMapper workScheduleMapper;
    private WorkScheduleServiceValidationImpl validator;

    @Override
    public WorkScheduleDto addWorkSchedule(WorkScheduleDto workScheduleDto) {
        validator.validateWorkScheduleDto(workScheduleDto);

        User user = userRepository.getByIdOrThrow(workScheduleDto.id());

        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(user);
        WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);

        log.info("WorkSchedule has been added successfully");

        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    public WorkScheduleDto updateWorkSchedule(long userId, long workScheduleId, WorkScheduleDto workScheduleDto) {
        validator.validateWorkScheduleDto(workScheduleDto);

        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);
        validator.validateUserAccess(userId, workSchedule);

        WorkSchedule newWorkSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        newWorkSchedule.setUser(workSchedule.getUser());
        WorkSchedule updatedWorkSchedule = workScheduleRepository.save(newWorkSchedule);

        log.info("WorkSchedule has been updated successfully");
        return workScheduleMapper.toWorkScheduleDto(updatedWorkSchedule);

    }

    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }
}

