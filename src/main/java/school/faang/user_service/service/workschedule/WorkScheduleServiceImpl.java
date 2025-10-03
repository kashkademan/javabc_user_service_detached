package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.dto.workschedule.UpdateWorkScheduleValidator;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.dto.workschedule.WorkScheduleValidator;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UpdateWorkScheduleMapper;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;
    private final UpdateWorkScheduleMapper updateWorkScheduleMapper;
    private final WorkScheduleValidator workScheduleValidator;
    private final UpdateWorkScheduleValidator updateWorkScheduleValidator;

    @Override
    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        workScheduleValidator.validate(workScheduleDto);

        User user = userRepository.getByIdOrThrow(userId);

        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(user);

        WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);

        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    @Override
    public UpdateWorkScheduleDto updateWorkSchedule(long userId, long workScheduleId, UpdateWorkScheduleDto updateWorkScheduleDto) {
        updateWorkScheduleValidator.validate(updateWorkScheduleDto);

        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);
        if (userId == workSchedule.getId()) {
            throw new ForbiddenException("Вы пытаетесь обновить чужие данные");
        }
            WorkSchedule updatedWorkSchedule = updateWorkScheduleMapper.toWorkSchedule(updateWorkScheduleDto);
            updatedWorkSchedule.setUser(workSchedule.getUser());

            workScheduleRepository.save(updatedWorkSchedule);

        return updateWorkScheduleMapper.toUpdateWorkScheduleDto(updatedWorkSchedule);
    }

    @Override
    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

}