package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
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

    @Override
    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User by id " + userId + " not Found"));
        if (isDateCheck(workScheduleDto)) {
            WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
            workScheduleDto.setUserId(user.getId());
            try {
                workSchedule = workScheduleRepository.save(workSchedule);
            } catch (Exception e) {
                log.error("Error saving work schedule " + e);
            }
            return workScheduleMapper.toWorkScheduleDto(workSchedule);
        }
        return workScheduleDto;
    }

    public boolean isDateCheck(WorkScheduleDto workScheduleDto) {
        LocalTime startTime = workScheduleDto.getStartTime();
        LocalTime startLunch = workScheduleDto.getStartLunch();
        LocalTime endLunch = workScheduleDto.getEndLunch();
        LocalTime endTime = workScheduleDto.getEndTIme();

        boolean startTimeAndLunch = startTime.isBefore(startLunch);
        boolean startLunchAndEndLunch = startLunch.isBefore(endLunch);
        boolean endLunchAndEndTime = endLunch.isBefore(endTime);

        if (startTimeAndLunch && startLunchAndEndLunch && endLunchAndEndTime) {
            return true;
        } else {
            new DataValidationException("Неккоректные данные");
            return false;
        }
    }
}
