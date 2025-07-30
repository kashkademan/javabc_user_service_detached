package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.GoalInvitationViewDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

/**
 * REST-контроллер для управления приглашениями к целям.
 * <p>
 * Обрабатывает создание, принятие, отклонение и получение списка приглашений с фильтрацией.
 * Все маршруты привязаны к конкретной цели через {@code goalId} в пути.
 * </p>
 *
 * @author Myrza
 * @since 08.07.2025
 */
@RestController
@RequestMapping("/goals-invitations")
@RequiredArgsConstructor
@Tag(name = "Приглашения к цели", description = "Управление приглашениями для целей")
public class GoalInvitationController {
    private final GoalInvitationService service;

    @PostMapping("/{goalId}")
    @Operation(summary = "Создать приглашение к цели", description = "Создает новое приглашение к указанной цели")
    public ResponseEntity<GoalInvitationViewDto> create(@PathVariable long goalId,
                                                        @Valid @RequestBody GoalInvitationCreateDto dto) {
        GoalInvitationViewDto invitation = service.create(goalId, dto);
        return ResponseEntity.ok(invitation);
    }

    @PostMapping("/{invitationId}/accept")
    @Operation(summary = "Принять приглашение", description = "Принимает приглашение по его идентификатору")
    public ResponseEntity<Void> accept(@PathVariable long invitationId) {
        service.accept(invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{invitationId}/reject")
    @Operation(summary = "Отклонить приглашение", description = "Отклоняет приглашение по его идентификатору")
    public ResponseEntity<Void> reject(@PathVariable long invitationId) {
        service.reject(invitationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Получить список приглашений", description = "Возвращает список приглашений по заданным фильтрам")
    public ResponseEntity<List<GoalInvitationViewDto>> getList(@ModelAttribute GoalInvitationFilterDto dto) {
        List<GoalInvitationViewDto> invitation = service.getByFilters(dto);
        return ResponseEntity.ok(invitation);
    }
}
