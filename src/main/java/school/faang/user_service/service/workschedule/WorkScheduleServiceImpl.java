package school.faang.user_service.service.workschedule;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;

    @Override
    public WorkScheduleDto addWorkSchedule(WorkScheduleDto workScheduleDto) {
        validateWorkScheduleDto(workScheduleDto);

        User user = userRepository.getByIdOrThrow(workScheduleDto.id());

        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(user);
        WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);

        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    public WorkScheduleDto updateWorkSchedule(long userId, long workScheduleId, WorkScheduleDto workScheduleDto) {
        validateWorkScheduleDto(workScheduleDto);

        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);
        validateUserAccess(userId, workSchedule);

        WorkSchedule newWorkSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        newWorkSchedule.setUser(workSchedule.getUser());
        WorkSchedule updatedWorkSchedule = workScheduleRepository.save(newWorkSchedule);

        return workScheduleMapper.toWorkScheduleDto(updatedWorkSchedule);

    }

    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    private void validateWorkScheduleDto(WorkScheduleDto workScheduleDto) {
        if (!(workScheduleDto.startTime().isBefore(workScheduleDto.startLunch())
                && workScheduleDto.startLunch().isBefore(workScheduleDto.endLunch())
                && workScheduleDto.endLunch().isBefore(workScheduleDto.endTime()))) {
            log.warn("Illegal schedule or lunch time. startTime: {}; endTime: {}; startLunch: {}; endLunch: {}",
                    workScheduleDto.startTime(), workScheduleDto.endTime(),
                    workScheduleDto.startLunch(), workScheduleDto.endLunch());
            throw new DataValidationException("Illegal schedule or lunch time.");
        }
    }

    private void validateUserAccess(long userId, WorkSchedule workSchedule) {
        if (!Objects.equals(workSchedule.getId(), userId)) {
            log.warn("user_id ({}) doesn't match workSchedule user_id ({})", workSchedule.getId(), userId);
            throw new ForbiddenException("User does not belong to this work schedule.");
        }
    }

}

