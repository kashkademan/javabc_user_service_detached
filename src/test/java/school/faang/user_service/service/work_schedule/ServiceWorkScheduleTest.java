package school.faang.user_service.service.work_schedule;

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

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private static final List<WorkSchedule> workScheduleList = Arrays.asList(new WorkSchedule(1L, LocalTime.of(9, 0),
                    LocalTime.of(17, 0), LocalTime.of(12, 30),
                    LocalTime.of(13, 0), "Europe/Moscow", new User()),
            new WorkSchedule(2L, LocalTime.of(8, 0), LocalTime.of(16, 0),
                    LocalTime.of(11, 0), LocalTime.of(12, 0),
                    "Europe/London", new User()));

    @BeforeEach
    void setUp() {
        WorkSchedule workSchedule = new WorkSchedule();
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setWorkSchedule(workSchedule);

        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    }

    @Test
    public void testAddWorkSchedule() {
        when(workScheduleRepository.save(any(WorkSchedule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkSchedule createWorkSchedule = workScheduleService.addWorkSchedule(workScheduleList.get(0));

        assertEquals(1L, createWorkSchedule.getId());
        verify(workScheduleRepository).save(createWorkSchedule);
    }

    @Test
    public void testAddWorkScheduleNotNull() {
        when(workScheduleRepository.save(any(WorkSchedule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        WorkSchedule saveWorkSchedule = workScheduleService.addWorkSchedule(workScheduleList.get(0));
        assertNotNull(saveWorkSchedule);
    }

    @Test
    public void testUpdateWorkScheduleDto() {
        when(workScheduleRepository.save(any(WorkSchedule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkSchedule newData = new WorkSchedule();
        newData.setStartTime(LocalTime.of(9, 0));
        newData.setEndTime(LocalTime.of(17, 0));
        newData.setStartLunch(LocalTime.of(12, 0));
        newData.setEndLunch(LocalTime.of(14, 0));
        newData.setTimezone("Asia/Tokyo");
        WorkSchedule result = workScheduleService.updateWorkScheduleDto(newData);

        assertEquals(LocalTime.of(9, 0), result.getStartTime());
        assertEquals(LocalTime.of(14, 0), result.getEndLunch());
        assertEquals("Asia/Tokyo", result.getTimezone());

        verify(workScheduleRepository).save(result);
    }


    @Test
    public void testGetByIdIfIdFound(){
        when(workScheduleRepository.findById(1L)).thenReturn(Optional.of(workScheduleList.get(0)));
        WorkSchedule resuleWorkSchedule = workScheduleService.getById(1L);

        assertEquals(1L, resuleWorkSchedule.getId());
        verify(workScheduleRepository).findById(resuleWorkSchedule.getId());
    }

    @Test
    public void testGetByIdIfIdNotFound(){
        long id = 3L;
        when(workScheduleRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(WorkScheduleNotFoundException.class, () ->
                workScheduleService.getById(id));
        String messageException = String.format(String.format("Work schedule not Found %d",
                        id));
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(messageException));
        verify(workScheduleRepository).findById(id);
    }
}
