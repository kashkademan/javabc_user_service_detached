package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.education.EducationServiceImpl;

import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EducationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationServiceImpl educationService;

    private EducationDto createEducationDto(int yearFrom) {
        EducationDto dto = new EducationDto();
        dto.setYearFrom(yearFrom);
        return dto;
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Education createEducation(User user) {
        Education education = new Education();
        education.setId(1L);
        education.setUser(user);
        return education;
    }

    @Test
    public void testAddEducationSuccess() {
        long userId = 1L;
        EducationDto dto = createEducationDto(Year.now().getValue() - 1);

        User user = createUser(userId);
        Education education = createEducation(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationMapper.toEntity(dto, user)).thenReturn(education);
        when(educationRepository.save(education)).thenReturn(education);
        when(educationMapper.toDto(education)).thenReturn(dto);

        EducationDto result = educationService.addEducation(userId, dto);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(userRepository).findById(userId);
        verify(educationRepository).save(education);
    }

    @Test
    public void testAddEducationWithUserNegativeIdThrowsException() {
        long userId = -2L;
        EducationDto dto = createEducationDto(2015);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> educationService.addEducation(userId, dto)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("User ID must be positive"));
    }
}