package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.rating_service.rating_aspect.ActionType;
import school.faang.user_service.rating_service.rating_aspect.RatingAction;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

/**
 * Контроллер для управления участием пользователей в событиях.
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *     <li>Регистрации пользователя на событие,</li>
 *     <li>Отмены участия пользователя в событии,</li>
 *     <li>Получения количество участников события,</li>
 *     <li>Получения списки участников события</li>
 * </ul>
 * </p>
 *
 * @author JekaCAP
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/participants")
@Tag(name = "Участие в мероприятиях", description = "Управление участниками событий")
public class EventParticipationController {
    private final EventParticipationService service;
    private final UserContext userContext;

    @PostMapping
    @RatingAction(ActionType.PARTICIPATION_IN_THE_EVENT)
    @Operation(summary = "Зарегистрироваться на событие", description = "Регистрирует текущего пользователя на событие")
    public ResponseEntity<Void> registerParticipant(@PathVariable long eventId) {
        long userId = userContext.getUserId();
        service.registerParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "Отменить регистрацию на событие", description = "Удаляет текущего пользователя из участников события")
    public ResponseEntity<Void> unregisterParticipant(@PathVariable long eventId) {
        long userId = userContext.getUserId();
        service.unregisterParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Получить всех участников события", description = "Возвращает список всех участников для указанного события")
    public ResponseEntity<List<UserDto>> getAllParticipants(@PathVariable long eventId) {
        return ResponseEntity.ok(service.getAllParticipantsByEventId(eventId));
    }

    @GetMapping("/count")
    @Operation(summary = "Посчитать участников события", description = "Возвращает количество участников указанного события")
    public ResponseEntity<CountResponse> countParticipants(@PathVariable long eventId) {
        return ResponseEntity.ok(service.countParticipantsByEventId(eventId));
    }
}