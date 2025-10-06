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
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.service.education.EducationService;

@RequestMapping("/education")
@RestController
@Slf4j
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;


    @PostMapping
    public EducationDto addEducation(@Valid @RequestBody CreateEducationDto createEducationDto) {

        return educationService.addEducation(createEducationDto);
    }

    @PatchMapping("/{educationId}")
    public EducationDto updateEducation(@Valid @RequestBody UpdateEducationDto updateEducationDto,
                                        @PathVariable long educationId) {

        return educationService.updateEducation(educationId, updateEducationDto);
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
