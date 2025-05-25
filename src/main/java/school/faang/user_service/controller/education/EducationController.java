package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.service.education.EducationService;

@Controller
@RequiredArgsConstructor
public class EducationController {
    private EducationService educationService;

    public EducationDto addEducation(long userId, EducationDto educationDto) {
        return educationService.addEducation(userId, educationDto);
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        return educationService.updateEducation(userId, educationDto);
    }

    public EducationDto getById(long educationId) {
        return educationService.getById(educationId);
    }
}
