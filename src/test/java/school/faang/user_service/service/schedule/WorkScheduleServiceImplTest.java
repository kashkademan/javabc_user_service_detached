package school.faang.user_service.service.schedule;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkScheduleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Spy
    private WorkScheduleMapperImpl workScheduleMapper;

    @InjectMocks
    private WorkScheduleServiceImpl service;

    @Captor
    private ArgumentCaptor<WorkSchedule> workScheduleCaptor;

    private Long userId;
    private Long anotherId;
    private Long workScheduleId;
    private WorkScheduleDto workScheduleDto;
    private WorkScheduleDto newWorkScheduleDto;
    private User user;
    private User anotheruser;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        user = User.builder().id(userId).build();
        anotherId = 2L;
        anotheruser = User.builder().id(anotherId).build();
        workScheduleId = 133L;
        workScheduleDto = WorkScheduleDto.builder()
                .id(workScheduleId)
                .startTime(LocalTime.of(8, 0))
                .build();
        newWorkScheduleDto = WorkScheduleDto.builder()
                .id(workScheduleId)
                .startTime(LocalTime.of(8, 30))
                .build();
    }

    @Test
    public void testAddWorkScheduleHavingWrongId() {
        when(userRepository.findById(userId)).thenThrow(new EntityNotFoundException(String.format
                ("User with id %d was not found", userId)));

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
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workScheduleRepository.findById(workScheduleId)).thenThrow(new EntityNotFoundException(String.format
                ("WorkSchedule with id %d was not found", workScheduleId)));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.updateWorkSchedule(userId, workScheduleDto);
        });
        assertEquals(String.format("WorkSchedule with id %d was not found", workScheduleId), exception.getMessage());
    }

    @Test
    void testUpdateNotTheirOwnWorkSchedule() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        WorkSchedule workScheduleEntity = workScheduleMapper.toWorkScheduleEntity(workScheduleDto);
        workScheduleEntity.setUser(anotheruser);
        when(workScheduleRepository.findById(workScheduleId))
                .thenReturn(Optional.of(workScheduleEntity));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.updateWorkSchedule(userId, workScheduleDto);
        });
        assertEquals("You can change only your own schedule", exception.getMessage());
    }

    @Test
    void testUpdateRightWorkSchedule() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        WorkSchedule workScheduleEntity = workScheduleMapper.toWorkScheduleEntity(workScheduleDto);
        workScheduleEntity.setUser(user);
        when(workScheduleRepository.findById(workScheduleId))
                .thenReturn(Optional.of(workScheduleEntity));

        service.updateWorkSchedule(userId, newWorkScheduleDto);
        verify(workScheduleRepository, times(1)).save(workScheduleCaptor.capture());
        assertEquals(newWorkScheduleDto.getStartTime(), workScheduleCaptor.getValue().getStartTime());
    }

    @Test
    void testGetByIdUsesScheduleRepository() {
        WorkSchedule workSchedule = WorkSchedule.builder().build();
        when(workScheduleRepository.findById(workScheduleId)).thenReturn(Optional.of(workSchedule));

        service.getById(workScheduleId);

        verify(workScheduleRepository, times(1)).findById(workScheduleId);
    }


    @Test
    void testGetByIdReturnsDto(){
        when(workScheduleRepository.findById(workScheduleId))
                .thenReturn(Optional.of(WorkSchedule.builder().build()));

        WorkScheduleDto result = service.getById(workScheduleId);

        assertEquals(WorkScheduleDto.class, result.getClass());
    }
}
