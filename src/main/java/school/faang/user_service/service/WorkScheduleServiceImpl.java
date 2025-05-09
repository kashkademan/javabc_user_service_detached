package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.WorkScheduleCreateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.WorkScheduleNotFoundException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;
    private final UserContext userContext;

    @Override
    @Transactional
    public WorkScheduleCreateDto addWorkSchedule(WorkScheduleCreateDto workScheduleCreateDto) {
        long userId = userContext.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User by id " + userId + " not Found"));
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleCreateDto);
        workSchedule.setUser(user);
        checkCorrectedWorkSchedule(workScheduleCreateDto);
        try {
            workSchedule = workScheduleRepository.save(workSchedule);
        } catch (Exception e) {
            log.error("Error saving work schedule " + e);
        }
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    @Override
    @Transactional
    public WorkScheduleCreateDto updateWorkScheduleDto(WorkScheduleCreateDto workScheduleCreateDto) {
        long userId = userContext.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(("User by Id not Found " + userId)));
        WorkSchedule workSchedule = user.getWorkSchedule();
        checkCorrectedWorkSchedule(workScheduleCreateDto);
        workScheduleMapper.update(workSchedule, workScheduleCreateDto);
        try {
            workSchedule = workScheduleRepository.save(workSchedule);
        } catch (Exception e) {
            log.error("Error saving work schedule " + e);
        }
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    @Override
    public WorkScheduleCreateDto getById(long workScheduleId) {
        WorkSchedule workSchedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() ->
                        new WorkScheduleNotFoundException(String.format("Work schedule not Found ",
                                workScheduleId)));
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    private void checkCorrectedWorkSchedule(WorkScheduleCreateDto workScheduleCreateDto) {
        LocalTime startTime = workScheduleCreateDto.getStartTime();
        LocalTime startLunch = workScheduleCreateDto.getStartLunch();
        LocalTime endLunch = workScheduleCreateDto.getEndLunch();
        LocalTime endTime = workScheduleCreateDto.getEndTime();

        boolean startTimeAndLunch = startTime.isBefore(startLunch);
        boolean startLunchAndEndLunch = startLunch.isBefore(endLunch);
        boolean endLunchAndEndTime = endLunch.isBefore(endTime);

        if (!(startTimeAndLunch && startLunchAndEndLunch && endLunchAndEndTime)) {
            throw new DataValidationException("Неккоректные данные");
        }
    }
}
