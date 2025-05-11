package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.work_schedule_dto.WorkScheduleCreateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.exception.work_schedule.WorkScheduleNotFoundException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;
import school.faang.user_service.validation.work_schedule_validation.WorkScheduleValidation;

import java.time.LocalTime;

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
                new UserNotFoundException(("User by Id not Found " + userId)));
        WorkSchedule workScheduleDataBase = user.getWorkSchedule();
        WorkScheduleValidation.checkCorrectedWorkSchedule(newWorkScheduleData);

        workScheduleDataBase.setStartTime(newWorkScheduleData.getStartTime());
        workScheduleDataBase.setStartLunch(newWorkScheduleData.getStartLunch());
        workScheduleDataBase.setEndLunch(newWorkScheduleData.getEndLunch());
        workScheduleDataBase.setEndTime(newWorkScheduleData.getEndTime());
        workScheduleDataBase.setTimezone(newWorkScheduleData.getTimezone());

        return workScheduleRepository.save(workScheduleDataBase);
    }

    @Override
    public WorkSchedule getById(long workScheduleId) {
        return workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() ->
                        new WorkScheduleNotFoundException(String.format("Work schedule not Found ",
                                workScheduleId)));
    }
}