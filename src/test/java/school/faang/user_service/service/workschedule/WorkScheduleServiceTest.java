package school.faang.user_service.service.workschedule;


import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapperImpl;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkScheduleServiceTest {
    private static final long USER_ID = 1L;
    private static final long ANOTHER_USER_ID = 2L;
    private static final long WORK_SCHEDULE_ID = 100L;
    private static final String ASIA_ALMATY_TIMEZONE = "Asia/Almaty";
    private static final String EUROPE_MOSCOW_TIMEZONE = "Europe/Moscow";

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Spy
    private WorkScheduleMapperImpl workScheduleMapper;

    @InjectMocks
    private WorkScheduleServiceImpl workScheduleService;

    @Test
    public void shouldAddWorkScheduleSuccessfully() {
        WorkScheduleDto scheduleDto = createValidWorkScheduleDto(ASIA_ALMATY_TIMEZONE);
        User user = createUser(USER_ID);
        WorkSchedule savedSchedule = createWorkScheduleEntity(scheduleDto, user);

        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(savedSchedule);

        WorkScheduleDto result = workScheduleService.addWorkSchedule(USER_ID, scheduleDto);

        assertNotNull(result);
        assertEquals(scheduleDto, result);
    }

    @Test
    public void shouldThrowValidationExceptionWhenAddingInvalidWorkSchedule() {
        WorkScheduleDto invalidScheduleDto = createInvalidWorkScheduleDto();

        assertThrows(DataValidationException.class,
                () -> workScheduleService.addWorkSchedule(USER_ID, invalidScheduleDto));
    }

    @Test
    public void shouldUpdateWorkScheduleSuccessfully() {
        WorkScheduleDto updatedScheduleDto = createValidWorkScheduleDto(EUROPE_MOSCOW_TIMEZONE);
        User user = createUser(USER_ID);
        WorkSchedule existingSchedule = createExistingWorkSchedule(user);
        WorkSchedule updatedSchedule = createWorkScheduleEntity(updatedScheduleDto, user);

        when(workScheduleRepository.getByIdOrThrow(WORK_SCHEDULE_ID)).thenReturn(existingSchedule);
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(updatedSchedule);

        WorkScheduleDto result = workScheduleService.updateWorkSchedule(USER_ID, WORK_SCHEDULE_ID, updatedScheduleDto);

        assertNotNull(result);
        assertEquals(updatedScheduleDto, result);
    }

    @Test
    public void shouldThrowValidationExceptionWhenUpdatingWithInvalidWorkSchedule() {
        WorkScheduleDto invalidScheduleDto = createInvalidWorkScheduleDto();

        assertThrows(DataValidationException.class,
                () -> workScheduleService.updateWorkSchedule(USER_ID, WORK_SCHEDULE_ID, invalidScheduleDto));
    }

    @Test
    public void shouldThrowForbiddenExceptionWhenUserAccessesAnotherUsersSchedule() {
        User anotherUser = createUser(ANOTHER_USER_ID);
        WorkScheduleDto scheduleDto = createValidWorkScheduleDto(EUROPE_MOSCOW_TIMEZONE);
        WorkSchedule workSchedule = createWorkScheduleEntity(scheduleDto, anotherUser);

        when(workScheduleRepository.getByIdOrThrow(WORK_SCHEDULE_ID)).thenReturn(workSchedule);

        assertThrows(ForbiddenException.class,
                () -> workScheduleService.updateWorkSchedule(USER_ID, WORK_SCHEDULE_ID, scheduleDto));
    }

    private WorkScheduleDto createValidWorkScheduleDto(String timezone) {
        return new WorkScheduleDto(
                WORK_SCHEDULE_ID,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                timezone
        );
    }

    private WorkScheduleDto createInvalidWorkScheduleDto() {
        return new WorkScheduleDto(
                WORK_SCHEDULE_ID,
                LocalTime.of(18, 0),  // startTime after endTime - invalid
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                EUROPE_MOSCOW_TIMEZONE
        );
    }

    private User createUser(long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    private WorkSchedule createWorkScheduleEntity(WorkScheduleDto dto, User user) {
        return new WorkSchedule(
                dto.id(),
                dto.startTime(),
                dto.endTime(),
                dto.startLunch(),
                dto.endLunch(),
                dto.timezone(),
                user
        );
    }

    private WorkSchedule createExistingWorkSchedule(User user) {
        return new WorkSchedule(
                WORK_SCHEDULE_ID,
                LocalTime.of(7, 0),
                LocalTime.of(15, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                ASIA_ALMATY_TIMEZONE,
                user
        );
    }
}
