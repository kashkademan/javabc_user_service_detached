package school.faang.user_service.controller.education;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

@Controller
@AllArgsConstructor
public class EducationController {

    EducationService educationService;
    UserContext userContext;

    public EducationDto addEducation(EducationDto educationDto) {

        validateEducation(educationDto);
        return educationService.addEducation(userContext.getUserId(), educationDto);
    }

    public EducationDto updateEducation(long educationId, EducationDto educationDto) {

        validateEducation(educationDto);
        return educationService.updateEducation(userContext.getUserId(),
                educationId,
                educationDto);
    }

    public EducationDto getById(long educationId) {
        return educationService.getById(educationId);
    }

    private void validateEducation(EducationDto educationDto) {
        if (educationDto.getYearFrom() != null
                && !educationDto.getInstitution().isBlank()) {
            throw new DataValidationException("Отсутствуют год поступления или учебное заведение");
        }
    }
}
