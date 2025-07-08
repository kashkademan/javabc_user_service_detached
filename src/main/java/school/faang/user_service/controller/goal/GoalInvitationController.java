package school.faang.user_service.controller.goal;

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
import school.faang.user_service.dto.goal.GoalInvitationDto;
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
@RequestMapping("goals/{goalId}/invitations")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService service;

    /**
     * Создает новое приглашение к цели.
     *
     * @param goalId идентификатор цели
     * @param dto    данные для создания приглашения
     * @return созданное приглашение в виде {@link GoalInvitationDto}
     */
    @PostMapping
    public ResponseEntity<GoalInvitationDto> create(@PathVariable long goalId,
                                                    @Valid @RequestBody GoalInvitationCreateDto dto) {
        GoalInvitationDto invitation = service.create(goalId, dto);
        return ResponseEntity.ok(invitation);
    }

    /**
     * Принимает приглашение по его идентификатору.
     *
     * @param invitationId идентификатор приглашения
     * @return пустой ответ с HTTP-статусом 200 OK
     */
    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<Void> accept(@PathVariable long invitationId) {
        service.accept(invitationId);
        return ResponseEntity.ok().build();
    }

    /**
     * Отклоняет приглашение по его идентификатору.
     *
     * @param invitationId идентификатор приглашения
     * @return пустой ответ с HTTP-статусом 200 OK
     */
    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<Void> reject(@PathVariable long invitationId) {
        service.reject(invitationId);
        return ResponseEntity.ok().build();
    }

    /**
     * Получает список приглашений с учетом фильтров.
     *
     * @param dto объект с параметрами фильтрации (например, inviterId, invitedId, status)
     * @return список приглашений в виде List<{@link GoalInvitationDto}>
     */
    @GetMapping("/search")
    public ResponseEntity<List<GoalInvitationDto>> getByFilters(@ModelAttribute GoalInvitationFilterDto dto) {
        List<GoalInvitationDto> invitation = service.getByFilters(dto);
        return ResponseEntity.ok(invitation);
    }
}
