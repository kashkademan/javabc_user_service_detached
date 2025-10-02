package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;

    @Override
    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        validatorWorkScheduleDto(workScheduleDto);

        User user = userRepository.getByIdOrThrow(userId);

        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(user);

        WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);

        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    private void validatorWorkScheduleDto(WorkScheduleDto dto) {
        if (dto.startTime() == null || dto.startLunch() == null ||
                dto.endLunch() == null || dto.endTime() == null) {
            throw new DataValidationException("Все временные поля должны быть заполнены");
        }

        if (!dto.startTime().isBefore(dto.startLunch())) {
            throw new DataValidationException("Стартовое время графика должно быть раньше времени старта обеда");
        }
        if (!dto.startLunch().isBefore(dto.endLunch())) {
            throw new DataValidationException("Время старта обеда должно быть раньше времени его окончания");
        }
        if (!dto.endLunch().isBefore(dto.endTime())) {
            throw new DataValidationException("Время конца обеда должно быть раньше времени конца графика");
        }
    }

}
