package school.faang.user_service.controller.event;

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
@RequestMapping("/events/{eventId}/participants")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;
    private final UserContext userContext;

    @PostMapping
    @RatingAction(ActionType.PARTICIPATION_IN_THE_EVENT)
    public ResponseEntity<Void> registerParticipant(@PathVariable long eventId) {
        long userId = userContext.getUserId();
        service.registerParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unregisterParticipant(@PathVariable long eventId) {
        long userId = userContext.getUserId();
        service.unregisterParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllParticipants(@PathVariable long eventId) {
        return ResponseEntity.ok(service.getAllParticipantsByEventId(eventId));
    }

    @GetMapping("/count")
    public ResponseEntity<CountResponse> countParticipants(@PathVariable long eventId) {
        return ResponseEntity.ok(service.countParticipantsByEventId(eventId));
    }
}
