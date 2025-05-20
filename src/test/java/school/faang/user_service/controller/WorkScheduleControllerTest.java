package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.service.WorkScheduleService;
import school.faang.user_service.util.WorkScheduleDtoValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkScheduleControllerTest {

    @Mock
    WorkScheduleService workScheduleService;

    @Mock
    WorkScheduleDtoValidator validator;

    @InjectMocks
    WorkScheduleController controller;

    private Long userId;
    private Long workScheduleId;
    private WorkScheduleDto workScheduleDto;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        workScheduleId = 133L;
        workScheduleDto = WorkScheduleDto.builder().
                id(workScheduleId).build();
    }

    @Test
    void testAddWorkSchedule_Correct() {
        when(workScheduleService.addWorkSchedule(userId, workScheduleDto)).thenReturn(workScheduleDto);

        WorkScheduleDto result = controller.addWorkSchedule(userId, workScheduleDto);

        assertEquals(workScheduleDto, result);
        verify(workScheduleService, times(1)).addWorkSchedule(userId, workScheduleDto);
    }

    @Test
    void testUpdateWorkSchedule() {
        doNothing().when(validator).validateDto(workScheduleDto);
        when(workScheduleService.updateWorkSchedule(userId, workScheduleDto)).thenReturn(workScheduleDto);

        WorkScheduleDto result = controller.updateWorkSchedule(userId, workScheduleDto);

        assertEquals(workScheduleDto, result);
        verify(workScheduleService, times(1)).updateWorkSchedule(userId, workScheduleDto);
    }

    @Test
    void testGetById() {
        when(workScheduleService.getById(workScheduleId)).thenReturn(workScheduleDto);
        WorkScheduleDto result = controller.getById(workScheduleId);

        assertEquals(workScheduleId, result.getId());
        verify(workScheduleService, times(1)).getById(workScheduleId);
    }
}