package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/education")
@Validated
public class EducationController {
    private final EducationService educationService;

    @PostMapping("/{userId}")
    public EducationDto addEducation(@PathVariable @Min(value = 1, message = "id must be a positive number") long userId,
                                     @RequestBody @Valid EducationDto educationDto) {
        return educationService.addEducation(userId, educationDto);
    }

    @PutMapping("/{userId}")
    public EducationDto updateEducation(@PathVariable @Min(value = 1, message = "id must be a positive number") long userId,
                                        @RequestBody @Valid EducationDto educationDto) {
        return educationService.updateEducation(userId, educationDto);
    }

    @GetMapping("/{educationId}")
    public EducationDto getById(@PathVariable @Min(value = 1, message = "id must be a positive number") long educationId) {
        return educationService.getById(educationId);
    }
}
