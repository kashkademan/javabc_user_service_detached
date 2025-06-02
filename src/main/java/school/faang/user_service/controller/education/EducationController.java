package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/education")
public class EducationController {
    private final EducationService educationService;

    @PostMapping("/{userId}")
    public EducationDto addEducation(@PathVariable long userId, @RequestBody EducationDto educationDto) {
        return educationService.addEducation(userId, educationDto);
    }

    @PutMapping("/{userId}")
    public EducationDto updateEducation(@PathVariable long userId, @RequestBody EducationDto educationDto) {
        return educationService.updateEducation(userId, educationDto);
    }

    @GetMapping("/{educationId}")
    public EducationDto getById(@PathVariable long educationId) {
        return educationService.getById(educationId);
    }
}
