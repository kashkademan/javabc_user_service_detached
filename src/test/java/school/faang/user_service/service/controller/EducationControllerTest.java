package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.education.EducationController;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.service.education.EducationService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EducationControllerTest {
    @Mock
    private EducationService educationService;

    @InjectMocks
    private EducationController educationController;

    @Test
    public void addEducationMethodCall() {
        educationController.addEducation(1, new EducationDto(1, 2023, 2024, "", "", ""));
        verify(educationService, times(1)).addEducation(1, new EducationDto(1, 2023, 2024, "", "", ""));
    }

    @Test
    public void updateEducationMethodCall() {
        educationController.updateEducation(1, new EducationDto(1, 2023, 2024, "", "", ""));
        verify(educationService, times(1)).updateEducation(1, new EducationDto(1, 2023, 2024, "", "", ""));
    }

    @Test
    public void getByIdMethodCall() {
        educationController.getById(1);
        verify(educationService, times(1)).getById(1);
    }
}
