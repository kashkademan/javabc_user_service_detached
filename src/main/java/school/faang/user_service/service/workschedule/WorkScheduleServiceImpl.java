package school.faang.user_service.service.workschedule;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleViewDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final WorkScheduleRepository repository;
    private final UserRepository userRepository;
    private final WorkScheduleMapper workScheduleMapper;
    private final UserContext context;

    @Override
    @Transactional
    public WorkScheduleViewDto addWorkSchedule(WorkScheduleCreateDto dto) {
        long currentUserId = context.getUserId();
        log.info("added Work Schedule");
        dto.validate();

        User user = userRepository.getByIdOrThrow(currentUserId);

        WorkSchedule workSchedule = workScheduleMapper.toEntity(dto, user);

        WorkSchedule saved = repository.save(workSchedule);

        return workScheduleMapper.toViewDto(saved);
    }

    @Override
    @Transactional
    public WorkScheduleViewDto updateWorkSchedule(long workScheduleId, WorkScheduleUpdateDto dto) {
        long currentUserId = context.getUserId();
        log.info("updated Work Schedule");
        dto.validate();

        User user = userRepository.getByIdOrThrow(currentUserId);
        WorkSchedule workSchedule = repository.getByIdOrThrow(workScheduleId);

        if (!workSchedule.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("User not allowed to update work schedule");
        }
        workScheduleMapper.update(dto, workSchedule);

        return workScheduleMapper.toViewDto(repository.save(workSchedule));
    }

    @Override
    @Transactional
    public WorkScheduleViewDto getById(long workScheduleId) {
        long currentUserId = context.getUserId();

        log.info("Getting Work Schedule by id: {} for userId={}", workScheduleId, currentUserId);

        WorkSchedule schedule = repository.getByIdOrThrow(workScheduleId);

        if (!schedule.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("You are not allowed to view this schedule");
        }

        return workScheduleMapper.toViewDto(schedule);
    }

    @Override
    @Transactional
    public void deleteWorkSchedule(long workScheduleId) {
        Long userId = context.getUserId();

        log.info("Deleting Work Schedule id={} for userId={}", workScheduleId, userId);

        WorkSchedule schedule = repository.getByIdOrThrow(workScheduleId);
        if (!schedule.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to delete this schedule");
        }

        repository.deleteById(workScheduleId);
    }
}