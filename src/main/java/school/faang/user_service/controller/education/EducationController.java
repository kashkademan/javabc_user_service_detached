package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.service.education.EducationService;

/**
 * EducationController для управления образованием пользователя.
 * <p>
 * Предоставляет эндпоинты для:
 *  * <ul>
 *  *     <li>Добавления образования,</li>
 *  *     <li>Обновления образования,</li>
 *  *     <li>Получения образования по его ID,</li>
 *  * </ul>
 * </p>*
 *
 * @author Пользователь
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
        EducationViewDto createdEducation = educationService.addEducation(userId, educationDto);
        return ResponseEntity.ok(createdEducation);
    }

    @PutMapping
    public ResponseEntity<EducationViewDto> updateEducation(long educationId,
                                                            @Valid @RequestBody EducationViewDto educationDto) {
        long userId = userContext.getUserId();
        EducationViewDto updateEducation = educationService.updateEducation(userId, educationId, educationDto);
        return ResponseEntity.ok(updateEducation);
    }

    @GetMapping
    public ResponseEntity<EducationViewDto> getAllEducation(long educationId) {
        return ResponseEntity.ok(educationService.getById(educationId));
    }
}
