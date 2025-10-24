package school.faang.user_service.service.career;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CareerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private CareerService careerService;

    @Test
    void addCareer_validDto_shouldReturnCareerDto() {
        LocalDate from = LocalDate.now().minusYears(2);
        LocalDate to = LocalDate.now().minusMonths(1);
        long requesterId = 100L;
        User user = new User();
        user.setId(requesterId);

        Career career = Career.builder()
                .id(1L)
                .dateFrom(from)
                .dateTo(to)
                .company("Google")
                .position("Software Engineer")
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(user);
        when(careerRepository.save(any(Career.class))).thenReturn(career);

        CreateCareerDto createDto = new CreateCareerDto(
                from, to, "Google", "Software Engineer"
        );
        CareerDto result = careerService.addCareer(createDto);
        CareerDto expectedDto = CareerMapper.toCareerDto(career);

        assertEquals(expectedDto, result);
    }

    @Test
    void addCareer_userNotFound_shouldThrowEntityNotFoundException() {
        CreateCareerDto createDto = new CreateCareerDto(
                LocalDate.now().minusYears(2),
                null,
                "Company",
                "Position"
        );

        long requesterId = 1L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.getByIdOrThrow(requesterId))
                .thenThrow(new EntityNotFoundException("User not found"));

        assertThrows(EntityNotFoundException.class, () -> careerService.addCareer(createDto));
    }

    @Test
    void getById_validId_shouldReturnCareerDto() {
        long careerId = 1L;
        Career career = Career.builder()
                .id(careerId)
                .dateFrom(LocalDate.of(2020, 1, 1))
                .dateTo(LocalDate.of(2023, 12, 31))
                .company("Apple")
                .position("Developer")
                .build();

        when(careerRepository.getByIdOrThrow(careerId)).thenReturn(career);

        CareerDto result = careerService.getById(careerId);
        CareerDto expectedDto = CareerMapper.toCareerDto(career);
        assertEquals(expectedDto, result);
    }

    @Test
    void getById_careerNotFound_shouldThrowEntityNotFoundException() {
        long careerId = 999L;
        when(careerRepository.getByIdOrThrow(careerId))
                .thenThrow(new EntityNotFoundException("Career not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> careerService.getById(careerId)
        );
        assertEquals("Career not found", exception.getMessage());
    }

    @Test
    void deleteCareer_validId_shouldDeleteCareer() {
        long requesterId = 100L;

        User user = new User();
        Career career = new Career();
        career.setUser(user);
        user.setId(requesterId);

        long careerId = 1L;

        when(userContext.getUserId()).thenReturn(requesterId);
        when(careerRepository.getByIdOrThrow(careerId)).thenReturn(career);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(user);

        careerService.deleteCareer(careerId);

        verify(careerRepository).delete(career);
    }

    @Test
    void deleteCareer_careerNotFound_shouldThrowEntityNotFoundException() {
        long careerId = 999L;
        long requesterId = 100L;

        when(userContext.getUserId()).thenReturn(requesterId);
        when(careerRepository.getByIdOrThrow(careerId))
                .thenThrow(new EntityNotFoundException("Career not found"));

        assertThrows(EntityNotFoundException.class, () -> careerService.deleteCareer(careerId));
    }

    @Test
    void deleteCareer_notOwner_shouldThrowAccessDeniedException() {
        long careerId = 1L;
        long ownerId = 5L;
        long requesterId = 9L;

        Career career = Career.builder()
                .id(careerId)
                .user(User.builder()
                        .id(ownerId)
                        .build())
                .build();

        User requester = User.builder().id(requesterId).build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(careerRepository.getByIdOrThrow(careerId)).thenReturn(career);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(requester);

        assertThrows(ForbiddenException.class, () ->
                careerService.deleteCareer(careerId));
    }

    @Test
    void updateCareer_validIdAndDto_shouldReturnCareerDto() {
        long careerId = 1L;
        long userId = 100L;

        Career career = Career.builder()
                .id(careerId)
                .user(User.builder()
                        .id(userId)
                        .build())
                .dateFrom(LocalDate.now().minusYears(1))
                .dateTo(LocalDate.now())
                .company("Google")
                .position("Software Engineer")
                .build();

        UpdateCareerDto updateDto = new UpdateCareerDto(
                career.getDateFrom(),
                null,
                "Meta",
                "Senior Engineer"
        );

        Career updateCareer = Career.builder()
                .id(careerId)
                .user(career.getUser())
                .dateFrom(career.getDateFrom())
                .dateTo(null)
                .company("Meta")
                .position("Senior Engineer")
                .build();

        when(userContext.getUserId()).thenReturn(userId);
        when(careerRepository.getByIdOrThrow(careerId)).thenReturn(career);
        when(careerRepository.save(any(Career.class))).thenReturn(updateCareer);

        CareerDto result = careerService.updateCareer(careerId, updateDto);
        CareerDto expectedDto = CareerMapper.toCareerDto(updateCareer);
        assertEquals(expectedDto, result);

        assertEquals("Meta", result.getCompany());
        assertEquals("Senior Engineer", result.getPosition());
        assertEquals(career.getDateFrom(), result.getFrom());
        assertNull(result.getTo());
    }

    @Test
    void updateCareer_careerNotFound_shouldThrowEntityNotFoundException() {
        long careerId = 999L;
        long userId = 100L;
        UpdateCareerDto updateDto = new UpdateCareerDto(
                LocalDate.now().minusYears(1),
                null,
                "Company",
                "Position"
        );

        when(userContext.getUserId()).thenReturn(userId);
        when(careerRepository.getByIdOrThrow(careerId))
                .thenThrow(new EntityNotFoundException("Career not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> careerService.updateCareer(careerId, updateDto)
        );
        assertEquals("Career not found", exception.getMessage());
    }
}