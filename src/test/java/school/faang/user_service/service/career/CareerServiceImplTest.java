package school.faang.user_service.service.career;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.CareerValidator;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CareerServiceImplTest {
    private final Long USER_ID = 1L;
    private final Long CAREER_ID = 1L;
    private final String COMPANY = "Test Company";
    private final String POSITION = "Test Position";
    private final LocalDate DATE_FROM = LocalDate.now().minusYears(1);
    private final LocalDate DATE_TO = LocalDate.now().minusDays(5);

    @Mock
    private UserRepository userRepository;
    @Mock
    private CareerRepository careerRepository;
    @Mock
    private CareerMapper careerMapper;
    @Mock
    private CareerValidator careerValidator;
    @InjectMocks
    private CareerServiceImpl careerService;

    private CareerDto testCareerDto;
    private Career testCareerEntity;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(USER_ID);

        testCareerDto = CareerDto.builder()
                .id(CAREER_ID)
                .company(COMPANY)
                .position(POSITION)
                .dateFrom(DATE_FROM)
                .dateTo(DATE_TO)
                .build();

        testCareerEntity = new Career();
        testCareerEntity.setId(CAREER_ID);
        testCareerEntity.setCompany(COMPANY);
        testCareerEntity.setPosition(POSITION);
        testCareerEntity.setDateFrom(DATE_FROM);
        testCareerEntity.setDateTo(DATE_TO);
        testCareerEntity.setUser(testUser);
    }

    @Test
    void testAddCareer_whenUserIdNotFound_thenThrowsException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> careerService.addCareer(USER_ID, testCareerDto));
        verify(careerValidator).validateDate(testCareerDto);
    }

    @Test
    void shouldPassCorrectEntityToRepository_whenAddCareer() {
        ArgumentCaptor<Career> careerCaptor = ArgumentCaptor.forClass(Career.class);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(careerMapper.toCareerEntity(testCareerDto)).thenReturn(testCareerEntity);
        when(careerRepository.save(any(Career.class))).thenReturn(testCareerEntity);
        when(careerMapper.toCareerDto(any(Career.class))).thenReturn(testCareerDto);

        CareerDto result = careerService.addCareer(USER_ID, testCareerDto);
        assertEquals(testCareerDto, result);

        verify(careerRepository).save(careerCaptor.capture());
        Career actualSavedCareer = careerCaptor.getValue();

        assertEquals(testCareerEntity, actualSavedCareer);
        assertEquals(testUser, actualSavedCareer.getUser());

        verify(careerValidator).validateDate(testCareerDto);
        verify(userRepository).findById(USER_ID);
        verify(careerMapper).toCareerEntity(testCareerDto);
        verify(careerMapper).toCareerDto(testCareerEntity);
    }

    @Test
    void shouldThrowException_whenUpdateCareerUserMismatch() {
        when(careerRepository.findById(CAREER_ID)).thenReturn(Optional.of(testCareerEntity));

        assertThrows(DataValidationException.class,
                () -> careerService.updateCareer(USER_ID, testCareerDto));
    }

    @Test
    void shouldSuccessfullyReturnCareerDto_whenUpdateCareer() {
        when(careerRepository.findById(CAREER_ID)).thenReturn(Optional.of(testCareerEntity));
        when(careerMapper.toCareerEntity(testCareerDto)).thenReturn(testCareerEntity);
        when(careerRepository.save(testCareerEntity)).thenReturn(testCareerEntity);
        when(careerMapper.toCareerDto(testCareerEntity)).thenReturn(testCareerDto);

        CareerDto result = careerService.updateCareer(USER_ID, testCareerDto);

        assertNotNull(result);
        assertEquals(testCareerDto, result);

        verify(careerValidator).validateDate(testCareerDto);
        verify(careerRepository).findById(CAREER_ID);
        verify(careerMapper).toCareerEntity(testCareerDto);
        verify(careerRepository).save(testCareerEntity);
        verify(careerMapper).toCareerDto(testCareerEntity);
    }

    @Test
    void shouldSuccessfullyReturnCareerDto_whenGetById() {
        when(careerRepository.findById(CAREER_ID)).thenReturn(Optional.of(testCareerEntity));
        when(careerMapper.toCareerDto(testCareerEntity)).thenReturn(testCareerDto);

        CareerDto result = careerService.getById(CAREER_ID);

        assertNotNull(result);
        assertEquals(testCareerDto, result);

        verify(careerRepository).findById(CAREER_ID);
        verify(careerMapper).toCareerDto(testCareerEntity);
    }
}
