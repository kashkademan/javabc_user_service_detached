package school.faang.user_service.service.work_schedule;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.exception.work_schedule.WorkScheduleNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;
import school.faang.user_service.service.WorkScheduleService;
import school.faang.user_service.service.WorkScheduleServiceImpl;

import java.time.LocalTime;;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceWorkScheduleTest {
    @Mock
    private WorkScheduleRepository workScheduleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private WorkScheduleServiceImpl workScheduleService;
    private final long userId = 1L;
    private final WorkSchedule workSchedule = new WorkSchedule();
    private final User user = new User();

    @Test
    public void testAddWorkSchedule() {
        user.setId(userId);
        user.setWorkSchedule(workSchedule);
        WorkSchedule workSchedule = new WorkSchedule(1L, LocalTime.of(9, 0),
                LocalTime.of(17, 0), LocalTime.of(12, 30),
                LocalTime.of(13, 0), "Europe/Moscow", new User());
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workScheduleRepository.save(any(WorkSchedule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkSchedule createWorkSchedule = workScheduleService.addWorkSchedule(workSchedule);

        assertEquals(1L, createWorkSchedule.getId());
        verify(workScheduleRepository).save(createWorkSchedule);
    }

    @Test
    public void testUpdateWorkScheduleDto() {
        user.setId(userId);
        user.setWorkSchedule(workSchedule);
        when(workScheduleRepository.save(any(WorkSchedule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        WorkSchedule newData = new WorkSchedule();
        newData.setStartTime(LocalTime.of(9, 0));
        newData.setEndTime(LocalTime.of(17, 0));
        newData.setStartLunch(LocalTime.of(12, 0));
        newData.setEndLunch(LocalTime.of(14, 0));
        newData.setTimezone("Asia/Tokyo");
        WorkSchedule result = workScheduleService.updateWorkScheduleDto(newData);

        assertEquals(LocalTime.of(9, 0), result.getStartTime());
        assertEquals(LocalTime.of(14, 0), result.getEndLunch());
        assertEquals(LocalTime.of(17, 0), result.getEndTime());
        assertEquals(LocalTime.of(12, 0), result.getStartLunch());
        assertEquals("Asia/Tokyo", result.getTimezone());
        verify(workScheduleRepository).save(result);
    }


    @Test
    public void testGetByIdIfIdFound() {
        WorkSchedule workSchedule = new WorkSchedule(1L, LocalTime.of(9, 0),
                LocalTime.of(17, 0), LocalTime.of(12, 30),
                LocalTime.of(13, 0), "Europe/Moscow", new User());

        when(workScheduleRepository.findById(1L)).thenReturn(Optional.of(workSchedule));
        WorkSchedule resuleWorkSchedule = workScheduleService.getById(1L);

        assertEquals(1L, resuleWorkSchedule.getId());
        verify(workScheduleRepository).findById(resuleWorkSchedule.getId());
    }

    @Test
    public void testGetByIdIfIdNotFound() {
        long id = 3L;

        when(workScheduleRepository.findById(id))
                .thenThrow(new WorkScheduleNotFoundException("Work schedule not Found " + id));

        assertThrows(WorkScheduleNotFoundException.class, ()-> workScheduleService.getById(id));
        verify(workScheduleRepository).findById(id);
    }
}
