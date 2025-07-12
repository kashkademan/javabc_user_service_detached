package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Controller;
import school.faang.user_service.controller.education.EducationController;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.service.education.EducationService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Controller
@ExtendWith(MockitoExtension.class)
public class EducationControllerTest {

    @Mock
    private EducationService educationService;

    @InjectMocks
    private EducationController educationController;

    private EducationDto createEducationDto() {
        EducationDto dto = new EducationDto();
        dto.setId(1L);
        dto.setInstitution("University");
        return dto;
    }

    @Test
    public void testAddEducation() {
        long userId = 1L;
        EducationDto dto = createEducationDto();

        when(educationService.addEducation(userId, dto)).thenReturn(dto);

        EducationDto result = educationController.addEducation(userId, dto);

       assertNotNull(result);
        assertEquals(dto, result);
        verify(educationService).addEducation(userId, dto);
    }

    @Test
    public void testUpdateEducation() {
        long userId = 1L;
        EducationDto dto = createEducationDto();

        when(educationService.updateEducation(userId, dto)).thenReturn(dto);

        EducationDto result = educationController.updateEducation(userId, dto);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(educationService).updateEducation(userId, dto);
    }

    @Test
    public void testGetById() {
        long educationId = 1L;
        EducationDto dto = createEducationDto();

        when(educationService.getById(educationId)).thenReturn(dto);

        EducationDto result = educationController.getById(educationId);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(educationService).getById(educationId);
    }

    @Test
    public void testGetByIdServiceThrowsException() {
        long id = -1L;
        when(educationService.getById(id)).thenThrow(new DataValidationException("Invalid education ID"));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> educationController.getById(id));

        assertEquals("Invalid education ID", exception.getMessage());
    }
}