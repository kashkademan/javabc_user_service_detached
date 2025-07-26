package school.faang.user_service.service.workschedule;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.mapper.WorkScheduleMapperImpl;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
public class WorkScheduleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Mock
    private WorkScheduleMapperImpl workScheduleMapper;  //всегда мапперImpl интерфейс

    @InjectMocks
    private WorkScheduleServiceImpl workScheduleService;  //всегда сервис, на который пишем тест


    @Test
    void testAddWorkSchedule() {
        //инициализация переменных (+/-/0 зависит от сценария теста)
        // Arrange: сначала инициализация
        long userId = 1;
        WorkScheduleDto dto = new WorkScheduleDto(userId,
                LocalTime.of(5, 0, 0),
                LocalTime.of(5, 10, 0),
                LocalTime.of(5, 1, 0),
                LocalTime.of(5, 9, 0),
                "florida");
        workScheduleService = new WorkScheduleServiceImpl(userRepository, workScheduleRepository, workScheduleMapper);

        // Act: вызов самого метода


        // Assert: проверка результата
        Assertions.assertEquals(dto, workScheduleService.addWorkSchedule(userId, dto), "Ошибка");
    }

    // + сецнарии этого теста.

    @Test
    void testUpdateWorkSchedule() {

    }

    @Test
    void testGetById() {

    }
}
