package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.career.CareerCreateDto;
import school.faang.user_service.dto.career.CareerViewDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.career.CareerServiceImpl;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CareerServiceImplTest {

    @InjectMocks
    private CareerServiceImpl careerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private CareerMapper careerMapper;

    private long userId = 1L;
    private long careerId = 100L;

    private User user;
    private Career career;
    private CareerViewDto careerViewDto;
    private CareerCreateDto careerCreateDto;
    private UpdateCareerDto updateCareerDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);

        career = new Career();
        career.setId(careerId);
        career.setUser(user);

        careerViewDto = new CareerViewDto(
                careerId,
                LocalDate.of(2020, 8, 19),
                LocalDate.of(2024, 10, 19),
                "Company",
                "Engineer"
        );

        careerCreateDto = new CareerCreateDto(
                LocalDate.of(2020, 8, 19),
                LocalDate.of(2024, 10, 19),
                "Company",
                "Engineer"
        );

        updateCareerDto = new UpdateCareerDto(
                LocalDate.of(2020, 8, 19),
                LocalDate.of(2024, 10, 19),
                "NewCompany",
                "Manager"
        );
    }

    @Test
    @DisplayName("career - Успешное создание новой карьеры")
    void addCareerTest() {
        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
        when(careerMapper.toEntity(careerCreateDto, user)).thenReturn(career);
        when(careerRepository.save(career)).thenReturn(career);
        when(careerMapper.toViewDto(career)).thenReturn(careerViewDto);

        CareerViewDto actual = careerService.career(userId, careerCreateDto);

        assertEquals(careerViewDto, actual);

        verify(userRepository).getByIdOrThrow(userId);
        verify(careerMapper).toEntity(careerCreateDto, user);
        verify(careerRepository).save(career);
        verify(careerMapper).toViewDto(career);
    }

    @Test
    @DisplayName("updateCareer - Успешное обновление карьеры")
    void updateCareerTest() {
        when(careerRepository.getByIdOrThrow(careerId)).thenReturn(career);
        doNothing().when(careerMapper).update(updateCareerDto, career);
        when(careerRepository.save(career)).thenReturn(career);
        when(careerMapper.toViewDto(career)).thenReturn(careerViewDto);

        CareerViewDto actual = careerService.updateCareer(userId, careerId, updateCareerDto);

        assertEquals(careerViewDto, actual);
        verify(careerRepository).getByIdOrThrow(careerId);
        verify(careerMapper).update(updateCareerDto, career);
        verify(careerRepository).save(career);
        verify(careerMapper).toViewDto(career);
    }

    @Test
    @DisplayName("getById: успешное получение")
    void getById_success() {
        when(careerRepository.getByIdOrThrow(careerId)).thenReturn(career);
        when(careerMapper.toViewDto(career)).thenReturn(careerViewDto);

        CareerViewDto result = careerService.getById(careerId);

        assertEquals(careerViewDto, result);
        verify(careerRepository).getByIdOrThrow(careerId);
        verify(careerMapper).toViewDto(career);
    }
}


