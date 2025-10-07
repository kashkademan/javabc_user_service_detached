package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.service.validator.UpdateWorkScheduleValidator;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.service.validator.WorkScheduleValidator;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;

    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        WorkScheduleValidator.validate(workScheduleDto);

        User user = userRepository.getByIdOrThrow(userId);

        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(user);

        workScheduleRepository.save(workSchedule);
        log.info("WorkSchedule for userID {} added", userId);

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    public WorkScheduleDto updateWorkSchedule(long userId, long workScheduleId,
                                              UpdateWorkScheduleDto updateWorkScheduleDto) {
        UpdateWorkScheduleValidator.validate(updateWorkScheduleDto);

        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);
        if (!workSchedule.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only update your own data");
        }
        workScheduleMapper.updateWorkSchedule(updateWorkScheduleDto, workSchedule);

        workScheduleRepository.save(workSchedule);
        log.info("WorkSchedule for userID {} updated", userId);

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

}