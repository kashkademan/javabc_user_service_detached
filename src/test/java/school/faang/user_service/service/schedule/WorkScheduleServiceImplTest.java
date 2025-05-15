package school.faang.user_service.service.schedule;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkScheduleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Spy
    private WorkScheduleMapper workScheduleMapper;

    @InjectMocks
    private WorkScheduleServiceImpl service;

    @Captor
    private ArgumentCaptor<WorkSchedule> workScheduleCaptor;

    private Long userId;
    private Long anotherId;
    private Long workScheduleId;
    private WorkScheduleDto workScheduleDto;
    private User user;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        user = User.builder().id(userId).build();
        anotherId = 2L;
        workScheduleId = 133L;
        workScheduleDto = WorkScheduleDto.builder().id(workScheduleId).build();
    }

    @Test
    public void testAddWorkScheduleHavingWrongId() {
        when(userRepository.findById(userId)).thenThrow(new EntityNotFoundException(String
                .format("User with id %d was not found", userId)));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.addWorkSchedule(userId, workScheduleDto);
        });
        assertEquals(String.format("User with id %d was not found", userId), exception.getMessage());
    }

    @Test
    public void testAddWorkScheduleHavingRightId() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        service.addWorkSchedule(userId, workScheduleDto);

        verify(userRepository, times(1)).findById(userId);
        verify(workScheduleRepository, times(1)).save(workScheduleCaptor.capture());
        assertEquals(user, workScheduleCaptor.getValue().getUser());
    }

    @Test
    void testUpdateWorkScheduleWithoutPreviousVersion() {
        when(workScheduleRepository.findById(workScheduleId)).thenThrow(new EntityNotFoundException(String
                .format("WorkSchedule with id %d was not found", workScheduleId)));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.updateWorkSchedule(userId, workScheduleDto);
        });
        assertEquals(String.format("WorkSchedule with id %d was not found", workScheduleId), exception.getMessage());
    }

    @Test
    void testUpdateNotTheirOwnWorkSchedule() {
        workScheduleDto.setId(anotherId);
        when(workScheduleRepository.findById(workScheduleId))
                .thenReturn(Optional.ofNullable(workScheduleMapper.toWorkScheduleEntity(workScheduleDto)));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.updateWorkSchedule(userId, workScheduleDto);
        });
        assertEquals("You can change only your own schedule", exception.getMessage());
    }




    @Test
    void getById() {
    }
}