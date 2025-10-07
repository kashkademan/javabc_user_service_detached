package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;
import school.faang.user_service.service.workschedule.WorkScheduleService;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkScheduleServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Mock
    private WorkScheduleMapper workScheduleMapper;

    @InjectMocks
    private WorkScheduleService workScheduleService;

    @Test
    public void testAddWorkSchedule() {
        long userId = 1L;
        WorkScheduleDto inputDto = new WorkScheduleDto(
                0L,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );
        WorkScheduleDto expectedOutputDto = new WorkScheduleDto(
                100L,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );
        User user = new User();
        WorkSchedule workSchedule = new WorkSchedule();

        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
        when(workScheduleMapper.toWorkSchedule(inputDto)).thenReturn(workSchedule);
        when(workScheduleRepository.save(workSchedule)).thenReturn(workSchedule);
        when(workScheduleMapper.toWorkScheduleDto(workSchedule)).thenReturn(expectedOutputDto);

        WorkScheduleDto result = workScheduleService.addWorkSchedule(userId, inputDto);

        assertEquals(expectedOutputDto, result);
        Mockito.verify(workScheduleRepository, Mockito.times(1))
                .save(workSchedule);

    }
}
