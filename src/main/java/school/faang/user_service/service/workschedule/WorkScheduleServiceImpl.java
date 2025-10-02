package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.dto.workschedule.WorkScheduleValidator;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;
    private final WorkScheduleValidator workScheduleValidator;

    @Override
    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        workScheduleValidator.validate(workScheduleDto);

        User user = userRepository.getByIdOrThrow(userId);

        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(user);

        WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);

        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

}