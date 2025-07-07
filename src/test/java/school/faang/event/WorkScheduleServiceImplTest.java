package school.faang.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleViewDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;
import school.faang.user_service.service.workschedule.WorkScheduleServiceImpl;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тесты WorkScheduleServiceImpl")
@ExtendWith(MockitoExtension.class)
public class WorkScheduleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Mock
    private WorkScheduleMapper workScheduleMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private WorkScheduleServiceImpl workScheduleService;

    @Test
    void addWorkSchedule_shouldSaveAndReturnViewDto() {
        long userId = 1L;
        WorkScheduleCreateDto createDto = new WorkScheduleCreateDto(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );

        User user = new User();
        WorkSchedule workSchedule = new WorkSchedule();
        WorkSchedule savedSchedule = new WorkSchedule();
        WorkScheduleViewDto expectedViewDto = new WorkScheduleViewDto(
                1L,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );

        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
        when(workScheduleMapper.toWorkSchedule(createDto)).thenReturn(workSchedule);
        when(workScheduleRepository.save(workSchedule)).thenReturn(savedSchedule);
        when(workScheduleMapper.toWorkScheduleDto(savedSchedule)).thenReturn(expectedViewDto);

        WorkScheduleViewDto actualDto = workScheduleService.addWorkSchedule(userId, createDto);

        assertEquals(expectedViewDto, actualDto);

        verify(userRepository).getByIdOrThrow(userId);
        verify(workScheduleMapper).toWorkSchedule(createDto);
        verify(workScheduleRepository).save(workSchedule);
        verify(workScheduleMapper).toWorkScheduleDto(savedSchedule);
    }

    @Test
    void updateWorkSchedule_shouldUpdateAndReturnDto() {
        long userId = 1L;
        long scheduleId = 10L;

        final WorkScheduleUpdateDto updateDto = new WorkScheduleUpdateDto(
                LocalTime.of(10, 0),
                LocalTime.of(19, 0),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                "Europe/Moscow"
        );

        User user = new User();
        user.setId(userId);

        WorkSchedule existing = new WorkSchedule();
        existing.setId(scheduleId);
        existing.setUser(user);

        WorkSchedule updated = new WorkSchedule();

        WorkScheduleViewDto expectedViewDto = new WorkScheduleViewDto(
                scheduleId,
                updateDto.startTime(),
                updateDto.endTime(),
                updateDto.startLunch(),
                updateDto.endLunch(),
                updateDto.timezone()
        );

        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
        when(workScheduleRepository.getByIdOrThrow(scheduleId)).thenReturn(existing);
        when(workScheduleRepository.save(existing)).thenReturn(updated);
        when(workScheduleMapper.toWorkScheduleDto(updated)).thenReturn(expectedViewDto);

        WorkScheduleViewDto result = workScheduleService.updateWorkSchedule(userId, scheduleId, updateDto);

        assertEquals(expectedViewDto, result);
        verify(workScheduleMapper).updateWorkScheduleFromDto(updateDto, existing);
    }


    @Test
    void getById_shouldReturnDto() {
        long scheduleId = 5L;
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        WorkSchedule schedule = new WorkSchedule();
        schedule.setId(scheduleId);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(18, 0));
        schedule.setStartTime(LocalTime.of(13, 0));
        schedule.setEndTime(LocalTime.of(14, 0));
        schedule.setTimezone("Europe/Moscow");
        schedule.setUser(user);

        WorkScheduleViewDto expectedDto = new WorkScheduleViewDto(
                scheduleId,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );

        when(workScheduleRepository.getByIdOrThrow(scheduleId)).thenReturn(schedule);
        when(userContext.getUserId()).thenReturn(userId);
        when(workScheduleMapper.toWorkScheduleDto(schedule)).thenReturn(expectedDto);

        WorkScheduleViewDto actual = workScheduleService.getById(scheduleId);

        assertEquals(expectedDto, actual);

        verify(workScheduleRepository).getByIdOrThrow(scheduleId);
        verify(userContext).getUserId();
        verify(workScheduleMapper).toWorkScheduleDto(schedule);
    }


    @Test
    void deleteWorkSchedule_shouldDeleteIfOwner() {
        long scheduleId = 99L;
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        WorkSchedule schedule = new WorkSchedule();
        schedule.setUser(user);

        when(workScheduleRepository.getByIdOrThrow(scheduleId)).thenReturn(schedule);
        when(userContext.getUserId()).thenReturn(userId);

        workScheduleService.deleteWorkSchedule(scheduleId);

        verify(workScheduleRepository).deleteById(scheduleId);
    }
}