package school.faang.user_service.service.career;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.CareerMapperImpl;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CareerServiceTest {

    private static final Long USER_ID = 10L;
    private static final Long CAREER_ID = 15L;
    private static final Long ANOTHER_USER_ID = 20L;
    private static final Long NON_EXISTENT_CAREER_ID = 35L;

    private static final String COMPANY = "company";
    private static final String POSITION = "position";
    private static final String OLD_COMPANY = "old company";
    private static final String OLD_POSITION = "old position";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_SPACES = "   ";

    private static final LocalDate START_DATE = LocalDate.of(2021, 6, 15);
    private static final LocalDate END_DATE = LocalDate.of(2023, 12, 31);
    private static final LocalDate OLD_START_DATE = LocalDate.of(2020, 1, 1);
    private static final LocalDate OLD_END_DATE = LocalDate.of(2022, 1, 1);
    private static final LocalDate INVALID_START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate INVALID_END_DATE = LocalDate.of(2022, 1, 1);

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private CareerMapperImpl careerMapper;

    @InjectMocks
    private CareerServiceImpl careerService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(USER_ID);
    }

    @Test
    void addCareer_WithNullStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                null,
                END_DATE,
                COMPANY,
                POSITION
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID, createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithNullCompanyThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                START_DATE,
                END_DATE,
                null,
                POSITION
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID, createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithBlankCompanyThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                START_DATE,
                END_DATE,
                BLANK_SPACES,
                POSITION
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID, createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithValidDataReturnsCareerDto() {
        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(testUser);
        when(careerRepository.save(any(Career.class))).thenAnswer((Answer<Career>) invocation -> {
            Career careerToSave = invocation.getArgument(0);
            careerToSave.setId(CAREER_ID);
            return careerToSave;
        });

        CreateCareerDto createDto = new CreateCareerDto(
                START_DATE,
                END_DATE,
                COMPANY,
                POSITION
        );

        CareerDto result = careerService.addCareer(USER_ID, createDto);
        verify(careerMapper).toCareer(createDto);
        verify(careerMapper).toCareerDto(any(Career.class));

        assertCareerDto(result);
        verify(careerRepository).save(any(Career.class));
        verify(userRepository).getByIdOrThrow(USER_ID);
    }

    @Test
    void addCareer_WithFutureStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.now().plusMonths(1),
                null,
                COMPANY,
                POSITION
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithEndDateBeforeStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                INVALID_START_DATE,
                INVALID_END_DATE,
                COMPANY,
                POSITION
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithEmptyCompanyThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                START_DATE,
                END_DATE,
                EMPTY_STRING,
                POSITION
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithEmptyPositionThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                START_DATE,
                END_DATE,
                COMPANY,
                EMPTY_STRING
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithCurrentStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.now(),
                END_DATE,
                COMPANY,
                POSITION
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID, createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void updateCareer_WithValidDataAndOwnerReturnsUpdatedCareerDto() {
        Career careerFromRepository = createCareerFromRepository();
        when(careerRepository.getByIdOrThrow(CAREER_ID)).thenReturn(careerFromRepository);
        Career updatedCareer = createUpdatedCareer();
        when(careerRepository.save(any(Career.class))).thenAnswer((Answer<Career>) invocation -> {
            Career careerToUpdate = invocation.getArgument(0);
            assertEquals(CAREER_ID, careerToUpdate.getId());
            assertEquals(USER_ID, careerToUpdate.getUser().getId());
            return careerToUpdate;
        });

        UpdateCareerDto updateDto = new UpdateCareerDto(
                START_DATE,
                END_DATE,
                COMPANY,
                POSITION
        );

        CareerDto result = careerService.updateCareer(USER_ID, CAREER_ID, updateDto);

        verify(careerMapper).toCareer(updateDto);
        verify(careerMapper).toCareerDto(any(Career.class));

        assertCareerDto(result);
        verify(careerRepository).getByIdOrThrow(CAREER_ID);
    }

    @Test
    void updateCareer_WhenCareerNotFoundThrowsDataValidationException() {
        UpdateCareerDto updateDto = new UpdateCareerDto(
                START_DATE,
                END_DATE,
                COMPANY,
                POSITION
        );

        when(careerRepository.getByIdOrThrow(NON_EXISTENT_CAREER_ID))
                .thenThrow(new DataValidationException("Career not found"));
        assertThrows(DataValidationException.class, () -> careerService.updateCareer(USER_ID,
                NON_EXISTENT_CAREER_ID, updateDto));
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateCareer_WhenUserNotOwnerThrowsForbiddenException() {
        Career careerFromRepository = createCareerWithDifferentUser();
        when(careerRepository.getByIdOrThrow(CAREER_ID)).thenReturn(careerFromRepository);

        UpdateCareerDto updateDto = new UpdateCareerDto(
                START_DATE,
                END_DATE,
                COMPANY,
                POSITION
        );

        assertThrows(ForbiddenException.class, () -> careerService.updateCareer(USER_ID,
                CAREER_ID, updateDto));
        verify(careerRepository, never()).save(any(Career.class));
    }

    @Test
    void getById_WithExistingCareerReturnsCareerDto() {
        Career career = createTestCareer();
        when(careerRepository.getByIdOrThrow(CAREER_ID)).thenReturn(career);

        CareerDto result = careerService.getById(CAREER_ID);

        verify(careerMapper).toCareerDto(career);
        assertCareerDto(result);
        verify(careerRepository).getByIdOrThrow(CAREER_ID);
    }

    @Test
    void getById_WhenCareerNotFoundThrowsDataValidationException() {
        when(careerRepository.getByIdOrThrow(NON_EXISTENT_CAREER_ID))
                .thenThrow(new DataValidationException("Career not found"));

        assertThrows(DataValidationException.class, () -> careerService.getById(NON_EXISTENT_CAREER_ID));
        verifyNoInteractions(userRepository);
    }

    private Career createTestCareer() {
        Career testCareer = new Career();
        testCareer.setId(CAREER_ID);
        testCareer.setUser(testUser);
        testCareer.setDateFrom(START_DATE);
        testCareer.setDateTo(END_DATE);
        testCareer.setCompany(COMPANY);
        testCareer.setPosition(POSITION);
        return testCareer;
    }

    private void assertCareerDto(CareerDto result) {
        assertEquals(COMPANY, result.company());
        assertEquals(POSITION, result.position());
        assertEquals(END_DATE, result.to());
        assertEquals(START_DATE, result.from());
        assertEquals(CAREER_ID, result.id());
    }

    private Career createCareerFromRepository() {
        Career careerFromRepository = new Career();
        careerFromRepository.setId(CAREER_ID);
        careerFromRepository.setUser(testUser);
        careerFromRepository.setDateFrom(OLD_START_DATE);
        careerFromRepository.setDateTo(OLD_END_DATE);
        careerFromRepository.setCompany(OLD_COMPANY);
        careerFromRepository.setPosition(OLD_POSITION);
        return careerFromRepository;
    }

    private Career createUpdatedCareer() {
        Career career = new Career();
        career.setId(CAREER_ID);
        career.setUser(testUser);
        career.setDateFrom(START_DATE);
        career.setDateTo(END_DATE);
        career.setCompany(COMPANY);
        career.setPosition(POSITION);
        return career;
    }

    private Career createCareerWithDifferentUser() {
        User anotherUser = new User();
        anotherUser.setId(ANOTHER_USER_ID);
        Career careerFromRepository = new Career();
        careerFromRepository.setId(CAREER_ID);
        careerFromRepository.setUser(anotherUser);
        careerFromRepository.setDateFrom(START_DATE);
        careerFromRepository.setDateTo(END_DATE);
        careerFromRepository.setCompany(COMPANY);
        careerFromRepository.setPosition(POSITION);
        return careerFromRepository;
    }
}
