package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.EducationDto.EducationDto;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequestMapping("/api/v1/user/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping("/{userId}/add")
    public ResponseEntity<EducationDto> addEducation(@PathVariable("userId") long userId,
                                                     @RequestBody EducationDto educationDto) {
        EducationDto addedEducation = educationService.addEducation(userId, educationDto);

        return new ResponseEntity<>(addedEducation, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<EducationDto> updateEducation(@PathVariable("userId") long userId,
                                                        @RequestBody EducationDto educationDto) {
        EducationDto updatedEducation = educationService.updateEducation(userId, educationDto);

        return new ResponseEntity<>(updatedEducation, HttpStatus.OK);
    }
    @GetMapping("/{educationId}")
    public ResponseEntity<EducationDto> getById(@PathVariable("educationId") long educationId) {

        EducationDto education = educationService.getById(educationId);

        return new ResponseEntity<>(education, HttpStatus.OK);
    }
}