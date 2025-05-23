package school.faang.user_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.controller.education.EducationController;
import school.faang.user_service.client.service.education.EducationService;
import school.faang.user_service.dto.EducationDto;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EducationControllerTest {

    @Mock
    EducationService educationService;

    @InjectMocks
    EducationController controller;

    private long userId;
    private long educationId;
    private EducationDto educationDto;

    @BeforeEach
    public void setup() {
        userId = 1L;
        educationId = 25L;
        educationDto = EducationDto.builder().build();
        educationService = new EducationService() {
            @Override
            public EducationDto addEducation(long userId, EducationDto educationDto) {
                return null;
            }

            @Override
            public EducationDto updateEducation(long userId, EducationDto educationDto) {
                return null;
            }

            @Override
            public EducationDto getById(long educationId) {
                return null;
            }
        };

    }

    @Test
    void testAddEducationCorrect() {
        when(educationService.addEducation(userId, educationDto)).thenReturn(educationDto);
        controller.addEducation(userId, educationDto);

    }
}
