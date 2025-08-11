package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.publisher.EventPublisher;
import school.faang.user_service.rating_service.rating_aspect.ActionType;
import school.faang.user_service.rating_service.rating_aspect.RatingAction;
import school.faang.user_service.service.event.EventService;

import java.util.List;

/**
 * Контроллер для управления пользовательскими событиями.
 * <p>
 * Предоставляет эндпоинты для создания, обновления, удаления и фильтрации событий.
 * </p>
 *
 * @author JekaCAP
 * @see school.faang.user_service.entity.event.Event
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Tag(name = "События", description = "Управление событиями")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @RatingAction(ActionType.ADD_EVENT)
    @Operation(summary = "Создать событие", description = "Создает новое событие и возвращает его DTO")
    public ResponseEntity<EventViewDto> create(@Valid @RequestBody EventCreateDto eventDto) {
        return ResponseEntity.ok(eventService.create(eventDto));
    }

    @PutMapping("/{eventId}")
    @Operation(summary = "Обновить событие", description = "Обновляет событие по идентификатору")
    public ResponseEntity<EventViewDto> update(@PathVariable long eventId,
                                               @Valid @RequestBody EventUpdateDto eventUpdateDto) {
        return ResponseEntity.ok(eventService.update(eventId, eventUpdateDto));
    }

    @GetMapping
    @Operation(summary = "Получить список событий", description = "Возвращает список событий по фильтрам")
    public ResponseEntity<List<EventViewDto>> getList(@Valid @ModelAttribute EventFilterDto filters) {
        return ResponseEntity.ok(eventService.getList(filters));
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Удалить событие", description = "Удаляет событие по идентификатору")
    public ResponseEntity<Void> delete(@PathVariable long eventId) {
        eventService.delete(eventId);
        return ResponseEntity.noContent().build();
    }
}