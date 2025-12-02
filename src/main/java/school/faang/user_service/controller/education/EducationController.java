package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.service.education.EducationService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EducationController {
    private final EducationService educationService;
    private final UserContext userContext;

    public EducationDto addEducation(@Valid EducationDto educationDto) {
        return educationService.addEducation(userContext.getUserId(), educationDto);
    }

    public EducationDto updateEducation(long educationId, @Valid EducationDto educationDto) {
        return educationService.updateEducation(userContext.getUserId(),
                educationId,
                educationDto);
    }

    public EducationDto getById(long educationId) {
        return educationService.getById(educationId);
    }

}
