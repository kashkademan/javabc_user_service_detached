package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.education.EducationServiceImpl;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceImplTest {

    @Mock
    EducationRepository educationRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    EducationMapper educationMapper;

    @InjectMocks
    EducationServiceImpl educationService;

    private final long userId = 1L;
    private final long educationId = 100L;

    private User user;
    private Education education;
    private CreateEducationDto createDto;
    private UpdateEducationDto updateDto;
    private EducationViewDto viewDto;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(userId);

        education = new Education();
        education.setId(educationId);
        education.setUser(user);

        createDto = new CreateEducationDto(2010, 2014, "MIT", "Bachelor", "CS");
        updateDto = new UpdateEducationDto(2010, 2015, "MIT", "Master", "CS");

        viewDto = new EducationViewDto(educationId, 2010, 2014, "MIT", "Bachelor", "CS");
    }

    @Test
    @DisplayName("addEducation: успешное добавление")
    void addEducation_success() {
        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
        when(educationMapper.toEntity(createDto, user)).thenReturn(education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toViewDto(education)).thenReturn(viewDto);

        EducationViewDto result = educationService.addEducation(userId, createDto);

        assertEquals(viewDto, result);
        verify(userRepository).getByIdOrThrow(userId);
        verify(educationMapper).toEntity(createDto, user);
        verify(educationRepository).save(education);
        verify(educationMapper).toViewDto(education);
    }

    @Test
    @DisplayName("addEducation: валидация года from - ошибка если год из будущего")
    void addEducation_futureYearFrom_throws() {
        CreateEducationDto badDto = new CreateEducationDto(Year.now().getValue() + 1, 2025, "MIT", "Bachelor", "CS");

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> educationService.addEducation(userId, badDto));
        assertEquals("You can't start learning in the future", ex.getMessage());

        verifyNoInteractions(userRepository, educationRepository, educationMapper);
    }

    @Test
    @DisplayName("updateEducation: успешное обновление")
    void updateEducation_success() {
        when(educationRepository.getByIdOrThrow(educationId)).thenReturn(education);
        doNothing().when(educationMapper).update(updateDto, education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toViewDto(education)).thenReturn(viewDto);

        EducationViewDto result = educationService.updateEducation(userId, educationId, updateDto);

        assertEquals(viewDto, result);
        verify(educationRepository).getByIdOrThrow(educationId);
        verify(educationMapper).update(updateDto, education);
        verify(educationRepository).save(education);
        verify(educationMapper).toViewDto(education);
    }

    @Test
    @DisplayName("updateEducation: ForbiddenException если пользователь не владелец")
    void updateEducation_forbidden() {
        User otherUser = new User();
        otherUser.setId(999L);
        education.setUser(otherUser);

        when(educationRepository.getByIdOrThrow(educationId)).thenReturn(education);

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> educationService.updateEducation(userId, educationId, updateDto));

        assertEquals("You can only update your education", ex.getMessage());

        verify(educationRepository).getByIdOrThrow(educationId);
        verifyNoMoreInteractions(educationRepository, educationMapper);
    }

    @Test
    @DisplayName("updateEducation: валидация года from - ошибка если год из будущего")
    void updateEducation_futureYearFrom_throws() {
        UpdateEducationDto badDto = new UpdateEducationDto(Year.now().getValue() + 1, 2025, "MIT", "Master", "CS");

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> educationService.updateEducation(userId, educationId, badDto));
        assertEquals("You can't start learning in the future", ex.getMessage());

        verifyNoInteractions(educationRepository, educationMapper);
    }

    @Test
    @DisplayName("getById: успешное получение")
    void getById_success() {
        when(educationRepository.getByIdOrThrow(educationId)).thenReturn(education);
        when(educationMapper.toViewDto(education)).thenReturn(viewDto);

        EducationViewDto result = educationService.getById(educationId);

        assertEquals(viewDto, result);
        verify(educationRepository).getByIdOrThrow(educationId);
        verify(educationMapper).toViewDto(education);
    }
}