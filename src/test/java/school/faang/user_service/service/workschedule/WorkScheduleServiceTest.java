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
    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Spy
    private WorkScheduleMapperImpl workScheduleMapper;

    @InjectMocks
    private WorkScheduleServiceImpl workScheduleService;


    @Test
    public void shouldAddWorkSchedule() {
        long userId = 1L;
        final WorkScheduleDto workScheduleDto = new WorkScheduleDto(
                100L,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                "Asia/Almaty"
        );

        User user = new User();
        user.setId(userId);

        final WorkSchedule saved = new WorkSchedule(
                100L,
                workScheduleDto.startTime(),
                workScheduleDto.endTime(),
                workScheduleDto.startLunch(),
                workScheduleDto.endLunch(),
                workScheduleDto.timezone(),
                user
        );

        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(saved);

        WorkScheduleDto result = workScheduleService.addWorkSchedule(userId, workScheduleDto);

        assertNotNull(result);
        assertEquals(workScheduleDto, result);
    }

    @Test
    public void shouldThrowValidationException_addWorkSchedule() {
        final WorkScheduleDto incorrectWorkScheduleDto = new WorkScheduleDto(
                100L,
                LocalTime.of(18, 0),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                "Europe/Moscow"
        );

        assertThrows(DataValidationException.class, () -> {
            workScheduleService.addWorkSchedule(
                    1L,
                    incorrectWorkScheduleDto);
        });
    }

    @Test
    public void shouldUpdateWorkSchedule() {
        long userId = 1L;
        long workScheduleId = 100L;
        final WorkScheduleDto workScheduleDto = new WorkScheduleDto(
                100L,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                "Europe/Moscow"
        );

        User user = new User();
        user.setId(userId);

        final WorkSchedule workSchedule = new WorkSchedule(
                100L,
                LocalTime.of(7, 0), LocalTime.of(15, 0),
                LocalTime.of(13, 0), LocalTime.of(14, 0),
                "Asia/Almaty",
                user
        );

        final WorkSchedule updatedWorkSchedule = new WorkSchedule(
                100L,
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                LocalTime.of(10, 0), LocalTime.of(12, 0),
                "Europe/Moscow",
                user
        );

        when(workScheduleRepository.getByIdOrThrow(workScheduleId)).thenReturn(workSchedule);
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(updatedWorkSchedule);

        WorkScheduleDto result = workScheduleService.updateWorkSchedule(userId, workScheduleId, workScheduleDto);

        assertNotNull(result);
        assertEquals(workScheduleDto, result);
    }

    @Test
    public void shouldThrowValidationException_updateWorkSchedule() {
        final WorkScheduleDto incorrectWorkScheduleDto = new WorkScheduleDto(
                100L,
                LocalTime.of(18, 0), LocalTime.of(8, 0),
                LocalTime.of(10, 0), LocalTime.of(12, 0),
                "Europe/Moscow"
        );

        assertThrows(DataValidationException.class, () -> {
            workScheduleService.updateWorkSchedule(
                    1L,
                    100L,
                    incorrectWorkScheduleDto);
        });
    }

    @Test
    public void shouldThrowForbiddenException() {
        User user = new User();
        user.setId(2L);

        final WorkScheduleDto workScheduleDto = new WorkScheduleDto(
                100L,
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                LocalTime.of(10, 0), LocalTime.of(12, 0),
                "Europe/Moscow"
        );

        final WorkSchedule workSchedule = new WorkSchedule(
                100L,
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                LocalTime.of(10, 0), LocalTime.of(12, 0),
                "Asia/Almaty",
                user
        );

        when(workScheduleRepository.getByIdOrThrow(100L)).thenReturn(workSchedule);

        assertThrows(ForbiddenException.class, () -> {
            workScheduleService.updateWorkSchedule(
                    1L,
                    100L,
                    workScheduleDto
            );
        });
    }
}