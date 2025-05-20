package school.faang.user_service.service.career;

import jakarta.persistence.EntityNotFoundException;
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

    private CareerDto createTestCareerDto() {
        return CareerDto.builder()
                .id(CAREER_ID)
                .company(COMPANY)
                .position(POSITION)
                .dateFrom(DATE_FROM)
                .dateTo(DATE_TO)
                .build();
    }

    private Career createTestCareerEntity() {
        User user = new User();
        user.setId(USER_ID);

        Career career = new Career();
        career.setId(CAREER_ID);
        career.setCompany(COMPANY);
        career.setPosition(POSITION);
        career.setDateFrom(DATE_FROM);
        career.setDateTo(DATE_TO);
        career.setUser(user);
        return career;
    }

    @Test
    void testAddCareer_whenUserIdNotFound_thenThrowsException() {
        User user = new User();
        user.setId(USER_ID);
        CareerDto dto = createTestCareerDto();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> careerService.addCareer(USER_ID, dto));

        verify(careerValidator).validateDate(dto);
    }

    @Test
    void shouldPassCorrectEntityToRepository_whenAddCareer() {
        CareerDto inputDto = createTestCareerDto();
        Career expectedCareer = createTestCareerEntity();
        User user = new User();
        user.setId(USER_ID);

        ArgumentCaptor<Career> careerCaptor = ArgumentCaptor.forClass(Career.class);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(careerMapper.toCareerEntity(inputDto)).thenReturn(expectedCareer);
        when(careerRepository.save(any(Career.class))).thenReturn(expectedCareer);
        when(careerMapper.toCareerDto(any(Career.class))).thenReturn(inputDto);

        careerService.addCareer(USER_ID, inputDto);

        verify(careerRepository).save(careerCaptor.capture());
        Career actualSavedCareer = careerCaptor.getValue();

        assertEquals(USER_ID, actualSavedCareer.getUser().getId());
        assertEquals(COMPANY, actualSavedCareer.getCompany());
        assertEquals(POSITION, actualSavedCareer.getPosition());
        assertEquals(DATE_FROM, actualSavedCareer.getDateFrom());
        assertEquals(DATE_TO, actualSavedCareer.getDateTo());

        verify(careerValidator).validateDate(inputDto);
        verify(userRepository).findById(USER_ID);
        verify(careerMapper).toCareerEntity(inputDto);
        verify(careerMapper).toCareerDto(expectedCareer);
    }

    @Test
    void shouldThrowException_whenUpdateCareerUserMismatch() {
        Career existingCareer = createTestCareerEntity();
        existingCareer.getUser().setId(5L); // Другой пользователь
        CareerDto inputDto = createTestCareerDto();

        when(careerRepository.findById(CAREER_ID)).thenReturn(Optional.of(existingCareer));

        assertThrows(DataValidationException.class,
                () -> careerService.updateCareer(USER_ID, inputDto));
    }

    @Test
    void shouldSuccessfullyReturnsCareerDto_whenUpdateCareer() {
        Career existingCareer = createTestCareerEntity();
        CareerDto inputDto = createTestCareerDto();
        Career updatedCareer = createTestCareerEntity();
        CareerDto expectedDto = createTestCareerDto();

        when(careerRepository.findById(CAREER_ID)).thenReturn(Optional.of(existingCareer));
        when(careerMapper.toCareerEntity(inputDto)).thenReturn(updatedCareer);
        when(careerRepository.save(updatedCareer)).thenReturn(updatedCareer);
        when(careerMapper.toCareerDto(updatedCareer)).thenReturn(expectedDto);

        CareerDto result = careerService.updateCareer(USER_ID, inputDto);

        assertNotNull(result);
        assertEquals(CAREER_ID, result.getId());
        assertEquals(COMPANY, result.getCompany());
        assertEquals(POSITION, result.getPosition());

        verify(careerValidator).validateDate(inputDto);
        verify(careerRepository).findById(CAREER_ID);
        verify(careerMapper).toCareerEntity(inputDto);
        verify(careerRepository).save(updatedCareer);
        verify(careerMapper).toCareerDto(updatedCareer);
    }

    @Test
    void shouldSuccessfullyReturnsCareerDto_whenGetById() {
        Career careerEntity = createTestCareerEntity();
        CareerDto expectedDto = createTestCareerDto();

        when(careerRepository.findById(CAREER_ID)).thenReturn(Optional.of(careerEntity));
        when(careerMapper.toCareerDto(careerEntity)).thenReturn(expectedDto);

        CareerDto result = careerService.getById(CAREER_ID);

        assertNotNull(result);
        assertEquals(CAREER_ID, result.getId());
        assertEquals(COMPANY, result.getCompany());
        assertEquals(POSITION, result.getPosition());

        verify(careerRepository).findById(CAREER_ID);
        verify(careerMapper).toCareerDto(careerEntity);
    }
}
