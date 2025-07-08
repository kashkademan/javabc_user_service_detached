package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.service.education.EducationService;

/**
 * EducationController — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author Пользователь
 * @since 04.07.2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping
public class EducationController {

    private final EducationService educationService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<EducationViewDto> addEducation(@Valid @RequestBody EducationViewDto educationDto) {
        long userId = userContext.getUserId();
        EducationViewDto created = educationService.addEducation(userId, educationDto);
        return ResponseEntity.ok(created);
    }
}
