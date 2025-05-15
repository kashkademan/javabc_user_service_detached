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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkScheduleControllerTest {

    @Mock
    WorkScheduleService workScheduleService;

    @Mock
    WorkScheduleDtoValidator validator;

    @InjectMocks
    WorkScheduleController controller;

    Long userId;
    Long workScheduleId;
    WorkScheduleDto workScheduleDto;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        workScheduleId = 133L;
        workScheduleDto = WorkScheduleDto.builder().build();
    }

    @Test
    void addWorkScheduleCorrect() {
        doNothing().when(validator).validateDto(workScheduleDto);

        controller.addWorkSchedule(userId, workScheduleDto);
        verify(workScheduleService, times(1)).addWorkSchedule(userId, workScheduleDto);
    }

    @Test
    void updateWorkSchedule() {
        doNothing().when(validator).validateDto(workScheduleDto);

        controller.updateWorkSchedule(userId, workScheduleDto);
        verify(workScheduleService, times(1)).updateWorkSchedule(userId, workScheduleDto);
    }

    @Test
    void getById() {
        controller.getById(workScheduleId);
        verify(workScheduleService, times(1)).getById(workScheduleId);
    }
}