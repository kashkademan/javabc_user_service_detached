package school.faang.user_service.controller.education;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.education.EducationService;

@RestController
@RequestMapping("/education")
@RequiredArgsConstructor
@Slf4j
public class EducationController {
    private final UserContext userContext;
    private final EducationService educationService;

    @PostMapping
    public EducationDto addEducation(@RequestBody CreateEducationDto dto) {
        log.debug("POST /addEducation dto={}", dto);
        if (dto.yearFrom() == null) {
            throw new DataValidationException("Не указан год начала обучения");
        }
        if (dto.institution() == null) {
            throw new DataValidationException("Обязательное поле");
        }
        String normalizedInstitution = dto.institution().trim();
        if (normalizedInstitution.isBlank()) {
            throw new DataValidationException("Название учебного заведения не может быть пустым");
        }
        long userId = userContext.getUserId();
        return educationService.addEducation(userId, dto);
    }

    @PatchMapping("/{educationId}")
    public EducationDto updateEducation(@PathVariable long educationId, @RequestBody UpdateEducationDto dto) {
        log.debug("PATCH /updateEducation educationId={}, dto={}", educationId, dto);
        if (dto.yearFrom() == null) {
            throw new DataValidationException("Обязательно, укажите дату начала обучения");
        }
        if (dto.institution() == null) {
            throw new DataValidationException("Обязательно, укажите место обучения");
        }
        String normalizedInstitution = dto.institution().trim();
        if (normalizedInstitution.isBlank()) {
            throw new DataValidationException("Обязательно, укажите место обучения");
        }
        long userId = userContext.getUserId();
        return educationService.updateEducation(userId, educationId, dto);
    }

    @GetMapping("/{educationId}")
    public EducationDto getEducationById(@PathVariable long educationId) {
        log.debug("GET /education/{}", educationId);
        return educationService.getById(educationId);
    }
}
