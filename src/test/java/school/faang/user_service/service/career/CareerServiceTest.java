package school.faang.user_service.service.career;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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
        testUser.setId(10L);
    }

    @Test
    void addCareer_WithNullStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                null,
                LocalDate.of(2023, 12, 31),
                "company",
                "position"
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(10L, createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithNullCompanyThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.of(2021, 6, 15),
                LocalDate.of(2023, 12, 31),
                null,
                "position"
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(10L, createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithBlankCompanyThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.of(2021, 6, 15),
                LocalDate.of(2023, 12, 31),
                "   ",
                "position"
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(10L, createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithValidDataReturnsCareerDto() {
        when(userRepository.getByIdOrThrow(10L)).thenReturn(testUser);
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.of(2021, 6, 15),
                LocalDate.of(2023, 12, 31),
                "company",
                "position"
        );

        Career testCareer = new Career();
        testCareer.setId(15L);
        testCareer.setUser(testUser);
        testCareer.setDateFrom(LocalDate.of(2021, 6, 15));
        testCareer.setDateTo(LocalDate.of(2023, 12, 31));
        testCareer.setCompany("company");
        testCareer.setPosition("position");

        when(careerRepository.save(any(Career.class))).thenReturn(testCareer);
        when(careerMapper.toCareerDto(testCareer)).thenReturn(
                new CareerDto(15L, LocalDate.of(2021, 6, 15),
                        LocalDate.of(2023, 12, 31), "company", "position")
        );

        CareerDto result = careerService.addCareer(10L, createDto);

        assertEquals("company", result.company());
        assertEquals("position", result.position());
        assertEquals(LocalDate.of(2023, 12, 31), result.to());
        assertEquals(LocalDate.of(2021, 6, 15), result.from());
        assertEquals(15L, result.id());

        verify(careerRepository).save(any(Career.class));
        verify(userRepository).getByIdOrThrow(10L);
    }

    @Test
    void addCareer_WithFutureStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.now().plusMonths(1),
                null,
                "company",
                "position"
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(10L,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithEndDateBeforeStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2022, 1, 1),
                "company",
                "position"
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(10L,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithEmptyCompanyThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.of(2021, 6, 15),
                LocalDate.of(2023, 12, 31),
                "",
                "position"
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(10L,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithEmptyPositionThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.of(2021, 6, 15),
                LocalDate.of(2023, 12, 31),
                "company",
                ""
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(10L,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithCurrentStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.now(),
                LocalDate.of(2023, 12, 31),
                "company",
                "position"
        );

        assertThrows(DataValidationException.class, () -> careerService.addCareer(10L, createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void updateCareer_WithValidDataAndOwnerReturnsUpdatedCareerDto() {
        Career careerFromRepository = new Career();
        careerFromRepository.setId(15L);
        careerFromRepository.setUser(testUser);
        careerFromRepository.setDateFrom(LocalDate.of(2020, 1, 1));
        careerFromRepository.setDateTo(LocalDate.of(2022, 1, 1));
        careerFromRepository.setCompany("old company");
        careerFromRepository.setPosition("old position");

        UpdateCareerDto updateDto = new UpdateCareerDto(
                LocalDate.of(2021, 6, 15),
                LocalDate.of(2023, 12, 31),
                "company",
                "position"
        );

        when(careerRepository.getByIdOrThrow(15L)).thenReturn(careerFromRepository);

        Career updatedCareer = new Career();
        updatedCareer.setId(15L);
        updatedCareer.setUser(testUser);
        updatedCareer.setDateFrom(LocalDate.of(2021, 6, 15));
        updatedCareer.setDateTo(LocalDate.of(2023, 12, 31));
        updatedCareer.setCompany("company");
        updatedCareer.setPosition("position");

        when(careerRepository.save(any(Career.class))).thenReturn(updatedCareer);
        when(careerMapper.toCareerDto(updatedCareer)).thenReturn(
                new CareerDto(15L, LocalDate.of(2021, 6, 15), LocalDate.of(
                        2023, 12, 31), "company", "position")
        );

        CareerDto result = careerService.updateCareer(10L, 15L, updateDto);

        assertEquals("company", result.company());
        assertEquals("position", result.position());
        assertEquals(LocalDate.of(2023, 12, 31), result.to());
        assertEquals(LocalDate.of(2021, 6, 15), result.from());
        assertEquals(15L, result.id());
        verify(careerRepository).save(any(Career.class));
        verify(careerRepository).getByIdOrThrow(15L);
    }

    @Test
    void updateCareer_WhenCareerNotFoundThrowsDataValidationException() {
        UpdateCareerDto updateDto = new UpdateCareerDto(
                LocalDate.of(2021, 6, 15),
                LocalDate.of(2023, 12, 31),
                "company",
                "position"
        );

        when(careerRepository.getByIdOrThrow(35L))
                .thenThrow(new DataValidationException("Career not found"));
        assertThrows(DataValidationException.class, () -> careerService.updateCareer(10L,
                35L, updateDto));
        verifyNoInteractions(userRepository, careerMapper);
    }

    @Test
    void updateCareer_WhenUserNotOwnerThrowsForbiddenException() {
        User anotherUser = new User();
        anotherUser.setId(20L);
        Career careerFromRepository = new Career();
        careerFromRepository.setId(15L);
        careerFromRepository.setUser(anotherUser);
        careerFromRepository.setDateFrom(LocalDate.of(2021, 6, 15));
        careerFromRepository.setDateTo(LocalDate.of(2023, 12, 31));
        careerFromRepository.setCompany("company");
        careerFromRepository.setPosition("position");

        UpdateCareerDto updateDto = new UpdateCareerDto(
                LocalDate.of(2021, 6, 15),
                LocalDate.of(2023, 12, 31),
                "company",
                "position"
        );
        when(careerRepository.getByIdOrThrow(15L)).thenReturn(careerFromRepository);

        assertThrows(ForbiddenException.class, () -> careerService.updateCareer(10L,
                15L, updateDto));
        verify(careerRepository, never()).save(any(Career.class));
    }

    @Test
    void getById_WithExistingCareerReturnsCareerDto() {
        Career career = new Career();
        career.setId(15L);
        career.setUser(testUser);
        career.setDateFrom(LocalDate.of(2021, 6, 15));
        career.setDateTo(LocalDate.of(2023, 12, 31));
        career.setCompany("company");
        career.setPosition("position");

        when(careerRepository.getByIdOrThrow(15L)).thenReturn(career);
        when(careerMapper.toCareerDto(career)).thenReturn(
                new CareerDto(15L, LocalDate.of(2021, 6, 15), LocalDate.of(
                        2023, 12, 31), "company", "position")
        );

        CareerDto result = careerService.getById(15L);

        assertEquals("company", result.company());
        assertEquals("position", result.position());
        assertEquals(LocalDate.of(2023, 12, 31), result.to());
        assertEquals(LocalDate.of(2021, 6, 15), result.from());
        assertEquals(15L, result.id());
        verify(careerRepository).getByIdOrThrow(15L);
    }

    @Test
    void getById_WhenCareerNotFoundThrowsDataValidationException() {
        when(careerRepository.getByIdOrThrow(35L))
                .thenThrow(new DataValidationException("Career not found"));

        assertThrows(DataValidationException.class, () -> careerService.getById(35L));
        verifyNoInteractions(userRepository, careerMapper);
    }
}