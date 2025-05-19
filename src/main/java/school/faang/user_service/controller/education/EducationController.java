package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EducationDto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.mappers.EducationMapper.EducationMapper;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequestMapping("/api/v1/user/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final EducationMapper educationMapper;

    @PostMapping("/{userId}/add")
    public ResponseEntity<EducationDto> addEducation(@PathVariable("userId") long userId,
                                                     @RequestBody EducationDto educationDto) {
        EducationDto addedEducation = educationService.addEducation(userId, educationDto);

        return new ResponseEntity<>(addedEducation, HttpStatus.CREATED);
    }

    @PatchMapping("/{educationId}")
    public ResponseEntity<EducationDto> updateEducation(@PathVariable long educationId,
                                                        @RequestBody EducationDto educationDto) {
        Education newEducationData = educationMapper.toEducation(educationDto);
        Education updatedEducation = educationService.updateEducation(educationId, newEducationData);
        EducationDto updatedEducationDto = educationMapper.toEducationDto(updatedEducation);
        return new ResponseEntity<>(updatedEducationDto, HttpStatus.OK);
    }

    @GetMapping("/{educationId}")
    public ResponseEntity<EducationDto> getById(@PathVariable("educationId") long educationId) {

        EducationDto education = educationService.getById(educationId);

        return new ResponseEntity<>(education, HttpStatus.OK);
    }
}