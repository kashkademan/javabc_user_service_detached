package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.service.education.EducationService;
import school.faang.user_service.dto.EducationDto;

@RestController
@RequestMapping("/api/v1/education")
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    @PostMapping
    public EducationDto addEducation(long userId, EducationDto educationDto) {
        return educationService.addEducation(userId, educationDto);
    }

    @PutMapping
    public EducationDto updateEducation(long userId, EducationDto educationDto) {
        return educationService.updateEducation(userId, educationDto);
    }

    @GetMapping
    public EducationDto getById(long educationId) {
        return educationService.getById(educationId);
    }
}
