package school.faang.user_service.controller.workschedule;

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
public class WorkScheduleController {
    private final WorkScheduleService service;

    @PostMapping
    public ResponseEntity<WorkScheduleViewDto> addWorkSchedule(
            @RequestBody @Valid WorkScheduleCreateDto workScheduleCreateDto) {
        WorkScheduleViewDto create = service.addWorkSchedule(workScheduleCreateDto);
        return ResponseEntity.ok(create);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkScheduleViewDto> updateWorkSchedule(
            @PathVariable long id,
            @RequestBody @Valid WorkScheduleUpdateDto dto) {
        WorkScheduleViewDto update = service.updateWorkSchedule(id, dto);
        return ResponseEntity.ok(update);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkScheduleViewDto> getWorkSchedule(@PathVariable long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkSchedule(@PathVariable long id) {
        service.deleteWorkSchedule(id);
        return ResponseEntity.noContent().build();
    }
}