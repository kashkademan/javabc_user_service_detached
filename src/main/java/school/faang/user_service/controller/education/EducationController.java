package school.faang.user_service.controller.education;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

@Controller
@RequiredArgsConstructor
@Data
public class EducationController {

    private static final EducationService educationService = null;
    private static long EducationDto;

    public static void addEducation() {
        EducationDto education;
        education = null;
        validateEducation(education);
        educationService.addEducation(EducationDto, education);
    }

    private static void validateEducation(EducationDto education) {
        if (education.getId() != 0) {
            throw new DataValidationException("Education id is blank");
        }
    }
}











