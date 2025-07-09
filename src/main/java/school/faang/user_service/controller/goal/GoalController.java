package school.faang.user_service.controller.goal;

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
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    @PostMapping
    public ResponseEntity<GoalDto> create(@Valid @RequestBody GoalCreateDto goalCreateDto) {
        return new ResponseEntity<>(service.create(goalCreateDto), HttpStatus.OK);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<GoalDto> update(@PathVariable long goalId, @Valid @RequestBody GoalUpdateDto goalUpdateDto) {
        return new ResponseEntity<>(service.update(goalId, goalUpdateDto), HttpStatus.OK);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<GoalDto> getById(@PathVariable long goalId) {
        return new ResponseEntity<>(service.getById(goalId), HttpStatus.OK);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> delete(@PathVariable long goalId) {
        service.delete(goalId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GoalDto>> getList(@Valid @ModelAttribute GoalFilterDto goalFilterDto) {
        return new ResponseEntity<>(service.getByFilters(goalFilterDto), HttpStatus.OK);
    }
}
