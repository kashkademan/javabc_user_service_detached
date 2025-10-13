package school.faang.user_service.service.workschedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkScheduleServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private WorkScheduleService workScheduleService;

    @Test
    public void addWorkSchedule_ShouldSetUserAndSaveWorkSchedule() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        WorkSchedule savedWorkSchedule = new WorkSchedule();
        savedWorkSchedule.setUser(user);

        WorkSchedule workSchedule = WorkSchedule.builder()
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .startLunch(LocalTime.of(13, 0))
                .endLunch(LocalTime.of(14, 0))
                .timezone("Europe/Moscow")
                .build();

        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
        when(workScheduleRepository.save(workSchedule)).thenReturn(savedWorkSchedule);

        WorkSchedule result = workScheduleService.addWorkSchedule(workSchedule);

        assertEquals(user, result.getUser());
        verify(workScheduleRepository, Mockito.times(1)).save(workSchedule);
    }

    @Test
    public void updateWorkSchedule_ShouldContextUserIsScheduleUser() {
        long workScheduleId = 1L;
        long contextUserId = 1L;
        long workScheduleUserId = 2L;

        User contextUser = new User();
        contextUser.setId(contextUserId);
        User workScheduleUser = new User();
        workScheduleUser.setId(workScheduleUserId);
        WorkSchedule existingWorkSchedule = WorkSchedule.builder()
                .id(workScheduleId)
                .user(workScheduleUser)
                .build();

        UpdateWorkScheduleDto updateDto = UpdateWorkScheduleDto.builder()
                .build();

        when(userContext.getUserId()).thenReturn(contextUserId);
        when(workScheduleRepository.getByIdOrThrow(workScheduleId)).thenReturn(existingWorkSchedule);

        assertThrows(ForbiddenException.class, () -> workScheduleService.updateWorkSchedule(workScheduleId, updateDto));
    }

    @Test
    public void updateWorkSchedule_ShouldUpdateWasCorrectAndSaved() {
        long userId = 1L;
        long workScheduleId = 1L;
        User user = new User();
        user.setId(userId);
        WorkSchedule existingWorkSchedule = WorkSchedule.builder()
                .id(workScheduleId)
                .user(user)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .startLunch(LocalTime.of(13, 0))
                .endLunch(LocalTime.of(14, 0))
                .timezone("Europe/Moscow")
                .build();

        UpdateWorkScheduleDto updateDto = UpdateWorkScheduleDto.builder()
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(19, 0))
                .startLunch(LocalTime.of(14, 0))
                .endLunch(LocalTime.of(15, 0))
                .timezone("Europe/Moscow")
                .build();

        when(userContext.getUserId()).thenReturn(userId);
        when(workScheduleRepository.getByIdOrThrow(workScheduleId)).thenReturn(existingWorkSchedule);
        when(workScheduleRepository.save(existingWorkSchedule)).thenReturn(existingWorkSchedule);

        WorkSchedule result = workScheduleService.updateWorkSchedule(workScheduleId, updateDto);

        assertEquals(updateDto.startTime(), result.getStartTime());
        assertEquals(updateDto.endTime(), result.getEndTime());
        assertEquals(updateDto.startLunch(), result.getStartLunch());
        assertEquals(updateDto.endLunch(), result.getEndLunch());
        assertEquals(updateDto.timezone(), result.getTimezone());
        verify(workScheduleRepository, Mockito.times(1)).save(existingWorkSchedule);
    }
}
