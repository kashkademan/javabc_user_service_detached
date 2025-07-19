package school.faang.user_service.controller.education;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.rating_service.rating_aspect.ActionType;
import school.faang.user_service.rating_service.rating_aspect.RatingAction;
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
 * @author fomchenkoandrey
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/educations")
public class EducationController {

    private final EducationService service;
    private final UserContext userContext;

    @PostMapping
    @RatingAction(ActionType.ADD_EDUCATION)
    public ResponseEntity<EducationViewDto> addEducation(@Valid @RequestBody CreateEducationDto educationDto) {
        long userId = userContext.getUserId();
        EducationViewDto createdEducation = service.addEducation(userId, educationDto);
        return ResponseEntity.ok(createdEducation);
    }

    @PutMapping("/{educationId}/count")
    public ResponseEntity<EducationViewDto> updateEducation(@PathVariable long educationId,
                                                            @Valid @RequestBody UpdateEducationDto educationDto) {
        long userId = userContext.getUserId();
        EducationViewDto updateEducation = service.updateEducation(userId, educationId, educationDto);
        return ResponseEntity.ok(updateEducation);
    }

    @GetMapping("/{educationId}")
    public ResponseEntity<EducationViewDto> getById(@PathVariable long educationId) {
        return ResponseEntity.ok(service.getById(educationId));
    }
}
