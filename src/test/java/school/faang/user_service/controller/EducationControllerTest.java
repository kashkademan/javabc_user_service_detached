package school.faang.user_service.controller;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.education.EducationController;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.service.education.EducationService;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EducationControllerTest {

    @Mock
    private EducationService educationService;

    @InjectMocks
    private EducationController educationController;

    @Test
    public void testAddEducation() {
        long userId = 1L;
        EducationDto dto = new EducationDto();
        dto.setId(1L);
        dto.setInstitution("University");

        when(educationService.addEducation(userId, dto)).thenReturn(dto);

        EducationDto result = educationController.addEducation(userId, dto);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(educationService).addEducation(userId, dto);
    }

    @Test
    public void testUpdateEducation() {
        long userId = 1L;
        EducationDto dto = new EducationDto();
        dto.setId(1L);
        dto.setInstitution("University");

        when(educationService.updateEducation(userId, dto)).thenReturn(dto);

        EducationDto result = educationController.updateEducation(userId, dto);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(educationService).updateEducation(userId, dto);
    }

    @Test
    public void testGetById() {
        long educationId = 1L;
        EducationDto dto = new EducationDto();
        dto.setId(1L);
        dto.setInstitution("University");

        when(educationService.getById(educationId)).thenReturn(dto);

        EducationDto result = educationController.getById(educationId);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(educationService).getById(educationId);
    }
}