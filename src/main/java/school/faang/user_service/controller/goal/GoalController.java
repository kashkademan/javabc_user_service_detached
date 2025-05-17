package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalCreateRequestDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.GoalUpdateRequestDto;
import school.faang.user_service.facade.goal.GoalFacade;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
@Slf4j
public class GoalController {
    private final GoalFacade goalFacade;
    @PostMapping
    public ResponseEntity<GoalResponseDto> createGoal(@RequestBody @Valid GoalCreateRequestDto goalCreateRequestDto) {
        log.info("Goal controller accepted request create goal {}", goalCreateRequestDto);
        GoalResponseDto response = goalFacade.createGoal(goalCreateRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED) ;
    }

    @PatchMapping
    public ResponseEntity<GoalResponseDto> updateGoal(@RequestBody @Valid GoalUpdateRequestDto goalUpdateRequestDto) {
        log.info("Goal controller accepted request update goal with id {}", goalUpdateRequestDto.getId());
        GoalResponseDto response = goalFacade.updateGoal(goalUpdateRequestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable long goalId) {
        log.info("Goal controller accepted request delete goal with id {}", goalId);
        goalFacade.deleteGoalById(goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subtasks/{goalParentId}")
    public ResponseEntity<List<GoalResponseDto>> getSubtasksByGoalId(@PathVariable long goalParentId) {
        log.info("Goal controller accepted request get subtasks with parent id {}", goalParentId);
        List<GoalResponseDto> response = goalFacade.getSubtasksByParentGoalId(goalParentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<GoalResponseDto>> getGoalsByUser(@RequestBody @Valid GoalFilterDto filterDto) {
        log.info("Goal controller accepted request get goats for user with filter {}", filterDto);
        List<GoalResponseDto> response = goalFacade.getGoalsByUserAndFilter(filterDto);
        return ResponseEntity.ok(response);
    }
}
