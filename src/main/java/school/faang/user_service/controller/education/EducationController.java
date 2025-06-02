package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EducationResponseDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    public EducationResponseDto addEducation(Long userId, EducationResponseDto educationResponseDto) {
        try {
            return educationService.addEducation(userId, educationResponseDto);
        } catch (DataValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public EducationResponseDto updateEducation(Long userId, EducationResponseDto educationResponseDto) {
        try {
            return educationService.updateEducation(userId, educationResponseDto);
        } catch (DataValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public EducationResponseDto getById(Long educationId) throws DataValidationException {
        return educationService.getById(educationId);
    }
}
