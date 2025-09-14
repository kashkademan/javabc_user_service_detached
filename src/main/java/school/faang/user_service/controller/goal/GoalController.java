package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import school.faang.user_service.dto.goal.GoalCompleteEvent;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.rating_service.rating_aspect.ActionType;
import school.faang.user_service.rating_service.rating_aspect.RatingAction;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
@Tag(name = "Цели", description = "Управление целями пользователя")
public class GoalController {
    private final GoalService service;

    @PostMapping
    @RatingAction(ActionType.ADD_GOAL)
    @Operation(summary = "Создать цель", description = "Создает новую цель и возвращает её DTO")
    public ResponseEntity<GoalDto> create(@Valid @RequestBody GoalCreateDto goalCreateDto) {
        return new ResponseEntity<>(service.create(goalCreateDto), HttpStatus.OK);
    }

    @PutMapping("/{goalId}")
    @Operation(summary = "Обновить цель", description = "Обновляет цель по её идентификатору")
    public ResponseEntity<GoalDto> update(@PathVariable long goalId, @Valid @RequestBody GoalUpdateDto goalUpdateDto) {
        return new ResponseEntity<>(service.update(goalId, goalUpdateDto), HttpStatus.OK);
    }

    @PutMapping("/{goalId}/complete")
    @Operation(summary = "Изменить статус", description = "Меняет статус задачи")
    public ResponseEntity<GoalCompleteEvent> complete(@PathVariable Long goalId) {
        service.completeGoal(goalId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{goalId}")
    @Operation(summary = "Получить цель по ID", description = "Возвращает цель по идентификатору")
    public ResponseEntity<GoalDto> getById(@PathVariable long goalId) {
        return new ResponseEntity<>(service.getById(goalId), HttpStatus.OK);
    }

    @DeleteMapping("/{goalId}")
    @Operation(summary = "Удалить цель", description = "Удаляет цель по идентификатору")
    public ResponseEntity<Void> delete(@PathVariable long goalId) {
        service.delete(goalId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    @Operation(summary = "Получить список целей", description = "Возвращает список целей по фильтрам")
    public ResponseEntity<List<GoalDto>> getList(@Valid @ModelAttribute GoalFilterDto goalFilterDto) {
        return new ResponseEntity<>(service.getByFilters(goalFilterDto), HttpStatus.OK);
    }
}