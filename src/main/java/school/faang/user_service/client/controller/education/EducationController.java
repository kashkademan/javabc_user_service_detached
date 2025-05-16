package school.faang.user_service.client.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.client.service.education.EducationServiceImpl;
import school.faang.user_service.dto.EducationDto;

@RestController
@RequiredArgsConstructor
public class EducationController {
    private final EducationServiceImpl educationService;

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
