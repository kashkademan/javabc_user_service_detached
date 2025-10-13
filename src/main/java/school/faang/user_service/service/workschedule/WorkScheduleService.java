package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;
import school.faang.user_service.service.validator.WorkScheduleValidator;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final UserContext userContext;

    public WorkSchedule addWorkSchedule(WorkSchedule workSchedule) {
        WorkScheduleValidator.validateEntity(workSchedule);

        long userId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);
        workSchedule.setUser(user);

        return workScheduleRepository.save(workSchedule);
    }

    public WorkSchedule updateWorkSchedule(long workScheduleId, WorkScheduleUpdateDto workScheduleUpdateDto) {
        WorkScheduleValidator.validateForUpdate(workScheduleUpdateDto);

        WorkSchedule workSchedule = getById(workScheduleId);
        long userId = userContext.getUserId();

        if (!Objects.equals(workSchedule.getUser().getId(), userId)) { //
            throw new ForbiddenException("You can only update your own data");
        }

        updateScheduleFields(workSchedule, workScheduleUpdateDto);

        return workScheduleRepository.save(workSchedule);
    }

    public WorkSchedule getById(long workScheduleId) {
        return workScheduleRepository.getByIdOrThrow(workScheduleId);
    }

    private void updateScheduleFields(WorkSchedule entity, WorkScheduleUpdateDto dto) {
        entity.setStartTime(dto.startTime());
        entity.setEndTime(dto.endTime());
        entity.setStartLunch(dto.startLunch());
        entity.setEndLunch(dto.endLunch());
        entity.setTimezone(dto.timezone());
    }
}