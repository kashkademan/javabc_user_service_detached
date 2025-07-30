package school.faang.user_service.controller.workschedule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleViewDto;
import school.faang.user_service.rating_service.rating_aspect.ActionType;
import school.faang.user_service.rating_service.rating_aspect.RatingAction;
import school.faang.user_service.service.workschedule.WorkScheduleService;

/**
 * WorkScheduleController для управления рабочим расписанием.
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *     <li>Добавления рабочего расписания,</li>
 *     <li>Обновления рабочего расписания,</li>
 *     <li>Получения рабочего расписания,</li>
 * </ul>
 * </p>
 *
 * @author JekaCAP
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/schedules")
@Tag(name = "Рабочее расписание", description = "Управление рабочим расписанием пользователя")
public class WorkScheduleController {
    private final WorkScheduleService service;

    @PostMapping
    @RatingAction(ActionType.ADD_WORKSCHEDULE)
    @Operation(summary = "Добавить рабочее расписание", description = "Создаёт новое расписание и возвращает его представление")
    public ResponseEntity<WorkScheduleViewDto> addWorkSchedule(
            @RequestBody @Valid WorkScheduleCreateDto workScheduleCreateDto) {
        WorkScheduleViewDto create = service.addWorkSchedule(workScheduleCreateDto);
        return ResponseEntity.ok(create);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить рабочее расписание", description = "Обновляет расписание по идентификатору")
    public ResponseEntity<WorkScheduleViewDto> updateWorkSchedule(
            @PathVariable long id,
            @RequestBody @Valid WorkScheduleUpdateDto dto) {
        WorkScheduleViewDto update = service.updateWorkSchedule(id, dto);
        return ResponseEntity.ok(update);
    }

    @Operation(summary = "Получить расписание", description = "Возвращает расписание по идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<WorkScheduleViewDto> getWorkSchedule(@PathVariable long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить расписание", description = "Удаляет расписание по идентификатору")
    public ResponseEntity<Void> deleteWorkSchedule(@PathVariable long id) {
        service.deleteWorkSchedule(id);
        return ResponseEntity.noContent().build();
    }
}