package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;
import school.faang.user_service.service.validator.WorkScheduleValidator;

import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final UserContext userContext;

    public WorkSchedule addWorkSchedule(WorkScheduleCreateDto workScheduleCreateDto) {
        WorkScheduleValidator.validateForCreate(workScheduleCreateDto);

        long userId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);
        WorkSchedule workSchedule = new WorkSchedule();
        createScheduleFields(workSchedule, workScheduleCreateDto);
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

    public void deleteWorkSchedule(long workScheduleId) {
        WorkSchedule workSchedule = getById(workScheduleId);
        long userId = userContext.getUserId();

        if (!Objects.equals(workSchedule.getUser().getId(), userId)) { //
            throw new ForbiddenException("You can only delete your own data");
        }

        workScheduleRepository.delete(workSchedule);
        System.out.printf("Work schedule of user: %s, has been deleted.", userId);
    }

    public WorkSchedule getById(long workScheduleId) {
        return workScheduleRepository.getByIdOrThrow(workScheduleId);
    }

    private void createScheduleFields(WorkSchedule entity, WorkScheduleCreateDto dto) {
        entity.setStartTime(dto.startTime());
        entity.setEndTime(dto.endTime());
        entity.setStartLunch(dto.startLunch());
        entity.setEndLunch(dto.endLunch());
        entity.setTimezone(dto.timezone());
    }

    private void updateScheduleFields(WorkSchedule entity, WorkScheduleUpdateDto dto) {
        updateIfNotNull(dto.startTime(), entity::setStartTime);
        updateIfNotNull(dto.endTime(), entity::setEndTime);
        updateIfNotNull(dto.startLunch(), entity::setStartLunch);
        updateIfNotNull(dto.endLunch(), entity::setEndLunch);
        updateIfNotNull(dto.timezone(), entity::setTimezone);
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}