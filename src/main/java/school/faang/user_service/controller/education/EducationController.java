package school.faang.user_service.controller.education;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.service.education.DataValidationException;
import school.faang.user_service.service.education.EducationService;

@RestController
public class EducationController {

    private final EducationService educationService;

    @Autowired
    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    public EducationDto addEducation(long userId, EducationDto educationDto) throws DataValidationException {
        return educationService.addEducation(userId, educationDto);
    }

    public EducationDto updateEducation(long userId, EducationDto educationDto) throws DataValidationException {
        return educationService.updateEducation(userId, educationDto);
    }

    public EducationDto getById(long educationId) throws DataValidationException {
        return educationService.getById(educationId);
    }
}
