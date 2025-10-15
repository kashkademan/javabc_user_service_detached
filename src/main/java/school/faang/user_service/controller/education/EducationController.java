package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.education.EducationUpdateDto;
import school.faang.user_service.dto.education.EducationCreateDto;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.service.education.EducationService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/education")
@RestController
public class EducationController {
    private final EducationService educationService;


    @PostMapping
    public EducationDto addEducation(@Valid @RequestBody EducationCreateDto educationCreateDto) {

        return educationService.addEducation(educationCreateDto);
    }

    @PatchMapping("/{educationId}")
    public EducationDto updateEducation(@PathVariable long educationId,
                                        @Valid @RequestBody EducationUpdateDto educationUpdateDto
    ) {
        return educationService.updateEducation(educationId, educationUpdateDto);
    }

    @GetMapping("/{educationId}")
    public EducationDto getById(@PathVariable long educationId) {
        return educationService.getById(educationId);
    }

    @DeleteMapping("/{educationId}")
    public EducationDto deleteEducation(@PathVariable long educationId) {
        return educationService.deleteEducation(educationId);
    }
}
