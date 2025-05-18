package school.faang.user_service.client.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.client.service.education.EducationServiceImpl;
import school.faang.user_service.dto.EducationDto;

@RestController
@RequiredArgsConstructor
public class EducationController {
    private final EducationServiceImpl educationService;

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
