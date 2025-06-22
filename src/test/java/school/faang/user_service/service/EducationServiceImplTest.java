package school.faang.user_service.service;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.education.EducationService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Service
@Data
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

    private EducationDto createEducationDto() {
        EducationDto dto = new EducationDto();
        dto.setId(1L);
        dto.setInstitution("University");
        dto.setYearFrom(2015);
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
        education.setInstitution("University");
        education.setYearFrom(2015);
        return education;
    }


    @Test
    public void testAddEducationSuccess() {
        long userId = 1L;
        EducationDto dto = createEducationDto();
        User user = createUser(userId);
        Education education = createEducation(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(educationRepository.save(any(Education.class))).thenReturn(education);

        EducationDto result = educationService.addEducation(userId, dto);

        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        verify(userRepository).findById(userId);
        verify(educationRepository).save(any(Education.class));
    }

    @Test
    public void testAddEducationWithNegativeUserIdThrowsException() {
        long userId = -5L;

        EducationDto dto = createEducationDto();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> educationService.addEducation(userId, dto));
    }

    @Test
    public void testUpdateEducationSuccess() {
         long userId = 1L;
         User user = createUser(userId);
         Education existingEducation = createEducation(user);

         EducationDto dto = createEducationDto();
         dto.setInstitution("Updated University");

         Education updatedEducation = createEducation(user);
         updatedEducation.setInstitution("Updated University");

         when(educationRepository.findById(1L)).thenReturn(Optional.of(existingEducation));
         when(educationRepository.save(updatedEducation)).thenReturn(updatedEducation);

         EducationDto result = educationService.updateEducation(userId, dto);

         assertNotNull(result);
         assertEquals("Updated University", result.getInstitution());
         assertEquals(userId, updatedEducation.getUser().getId());
         verify(educationRepository).save(updatedEducation);
    }

    @Test
    public void testUpdateEducationNotFoundThrowsException() {
        long userId = 1L;
        EducationDto dto = createEducationDto();

        when(educationRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> educationService.updateEducation(userId, dto));

        assertTrue(exception.getMessage().contains("Education with id=%d not found"));
    }

    @Test
    public void testGetByIdSuccess() {
        long educationId = 1L;
        Education education = createEducation(createUser(1L));
        EducationDto dto = createEducationDto();

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

        assertTrue(exception.getMessage().contains("Education with id=%d not found"));
    }

    @Test
    public void testGetByIdInvalidIdThrowsException() {
        Long invalidId = -1L;

        DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> educationService.getById(invalidId));

        assertEquals("Invalid education ID", exception.getMessage());
    }
}