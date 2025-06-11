package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.education.EducationService;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EducationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @Spy
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationService educationService;

    @Test
    public void testAddEducationSuccess() {
        long userId = 1L;
        EducationDto dto = new EducationDto();
        dto.setId(1L);
        dto.setYearFrom(2015);
        dto.setInstitution("University");

        User user = new User();
        user.setId(userId);

        Education education = new Education();
        education.setId(1L);
        education.setUser(user);
        education.setInstitution("University");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationRepository.save(education)).thenReturn(education);

        EducationDto result = educationService.addEducation(userId, dto);

        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        verify(userRepository).findById(userId);
        verify(educationRepository).save(education);
    }

    @Test
    public void testAddEducationUserNotFoundThrowsException() {
        long userId = 1L;
        EducationDto dto = new EducationDto();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> educationService.addEducation(userId, dto));

        assertTrue(exception.getMessage().contains("User with id=%d not found"));
    }

    @Test
    public void testUpdateEducationSuccess() {
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        Education existingEducation = new Education();
        existingEducation.setId(1L);
        existingEducation.setUser(user);

        EducationDto dto = new EducationDto();
        dto.setId(1L);
        dto.setInstitution("Updated University");

        Education updatedEducation = new Education();
        updatedEducation.setId(1L);
        updatedEducation.setInstitution("Updated University");
        updatedEducation.setUser(user);

        when(educationRepository.findById(1L)).thenReturn(Optional.of(existingEducation));
        when(educationRepository.save(updatedEducation)).thenReturn(updatedEducation);


        EducationDto result = educationService.updateEducation(userId, dto);

        assertNotNull(result);
        assertEquals(dto.getInstitution(), result.getInstitution());
        assertEquals(userId, updatedEducation.getUser().getId());
        verify(educationRepository).save(updatedEducation);
    }

    @Test
    public void testUpdateEducationNotFoundThrowsException() {
        long userId = 1L;
        EducationDto dto = new EducationDto();
        dto.setEducationLevel("Bachelor");
        when(educationRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> educationService.updateEducation(userId, dto));

        assertTrue(exception.getMessage().contains("User with id=%d not found"));
    }

    @Test
    public void testGetByIdSuccess() {
        long educationId = 1L;

        Education education = new Education();
        education.setId(educationId);
        education.setInstitution("University");

        EducationDto dto = new EducationDto();
        dto.setId(educationId);
        dto.setInstitution("University");

        when(educationRepository.findById(educationId)).thenReturn(Optional.of(education));

        EducationDto result = educationService.getById(educationId);

        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getInstitution(), result.getInstitution());

        verify(educationRepository).findById(educationId);
    }

    @Test
    public void testGetByIdNotFoundThrowsException() {
        long educationId = 1L;
        when(educationRepository.findById(educationId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> educationService.getById(educationId));

        assertTrue(exception.getMessage().contains("User with id=%d not found"));
    }
}