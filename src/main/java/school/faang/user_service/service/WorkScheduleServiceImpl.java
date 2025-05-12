package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.exception.work_schedule.WorkScheduleNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;
import school.faang.user_service.validation.work_schedule_validation.WorkScheduleValidation;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final UserContext userContext;

    @Override
    @Transactional
    public WorkSchedule addWorkSchedule(WorkSchedule workSchedule) {
        long userId = userContext.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User by id %d not Found", userId)));
        workSchedule.setUser(user);
        WorkScheduleValidation.checkCorrectedWorkSchedule(workSchedule);
        return workScheduleRepository.save(workSchedule);
    }

    @Override
    @Transactional
    public WorkSchedule updateWorkScheduleDto(WorkSchedule newWorkScheduleData) {
        long userId = userContext.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User by Id %d not Found ", userId)));
        WorkSchedule workScheduleDataBase = user.getWorkSchedule();
        WorkScheduleValidation.checkCorrectedWorkSchedule(newWorkScheduleData);

        executeIfNotNull(newWorkScheduleData.getStartTime(),
                () -> workScheduleDataBase.setStartTime(newWorkScheduleData.getStartTime()));
        executeIfNotNull(newWorkScheduleData.getStartLunch(),
                () -> workScheduleDataBase.setStartLunch(newWorkScheduleData.getStartLunch()));
        executeIfNotNull(newWorkScheduleData.getEndLunch(),
                () -> workScheduleDataBase.setEndLunch(newWorkScheduleData.getEndLunch()));
        executeIfNotNull(newWorkScheduleData.getEndTime(),
                () -> workScheduleDataBase.setEndTime(newWorkScheduleData.getEndTime()));
        executeIfNotNull(newWorkScheduleData.getTimezone(),
                () -> workScheduleDataBase.setTimezone(newWorkScheduleData.getTimezone()));
        return workScheduleRepository.save(workScheduleDataBase);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkSchedule getById(long workScheduleId) {
        return workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() ->
                        new WorkScheduleNotFoundException(String.format("Work schedule not Found %d",
                                workScheduleId)));
    }

    private void executeIfNotNull(Object field, Runnable runnable) {
        if (Objects.nonNull(field)) {
            runnable.run();
        }
    }
}