package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.IndexGoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalDto>> index(
            @RequestBody IndexGoalDto dto
    ) {
        List<GoalDto> result = goalService.get(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @PostMapping
    public ResponseEntity<GoalDto> create(
            @Valid @RequestBody
            CreateGoalDto createGoalDto

    ) {
        GoalDto result = goalService.create(createGoalDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<List<GoalDto>> delete(
            @PathVariable("id") Long id
    ) {
        goalService.delete(id);
        return ResponseEntity
                .ok().build();
    }


    @PatchMapping("{id}")
    public ResponseEntity<GoalDto> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateGoalDto updateGoalDto
    ) {
        GoalDto result = goalService.update(id, updateGoalDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
