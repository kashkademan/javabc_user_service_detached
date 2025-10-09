package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final UserContext userContext;

    public WorkSchedule addWorkSchedule(WorkSchedule workSchedule) {
        long userId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);
        workSchedule.setUser(user);

        return workScheduleRepository.save(workSchedule);
    }

    public WorkSchedule updateWorkSchedule(long workScheduleId) {
        long userId = userContext.getUserId();
        WorkSchedule workSchedule = getById(workScheduleId);

        if (!workSchedule.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only update your own data");
        }

        return workScheduleRepository.save(workSchedule);
    }

    public WorkSchedule getById(long workScheduleId) {
        return workScheduleRepository.getByIdOrThrow(workScheduleId);
    }

    public WorkSchedule save(WorkSchedule workSchedule) {
        return workScheduleRepository.save(workSchedule);
    }
}