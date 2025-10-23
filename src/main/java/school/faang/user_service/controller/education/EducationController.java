package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.service.education.EducationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/education")
public class EducationController {

    private final EducationService educationService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<EducationDto> addEducation(@RequestBody @Valid EducationDto educationDto) {
        EducationDto response = educationService.addEducation(userContext.getUserId(), educationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{educationId}")
    public ResponseEntity<EducationDto> updateEducation(@PathVariable Long educationId, @RequestBody @Valid EducationDto educationDto) {
        Long currentUserId = userContext.getUserId();

        EducationDto response = educationService.updateEducation(currentUserId, educationId, educationDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{educationId}")
    public ResponseEntity<EducationDto> getById(@PathVariable Long educationId) {
        EducationDto response =  educationService.getById(educationId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
