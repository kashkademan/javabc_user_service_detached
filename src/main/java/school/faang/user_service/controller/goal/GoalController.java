package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public GoalDto create(@Valid @RequestBody GoalCreateDto goalCreateDto) {
        return service.create(goalCreateDto);
    }

    @PutMapping("/{goalId}")
    public GoalDto update(@PathVariable long goalId, @Valid @RequestBody GoalUpdateDto goalUpdateDto) {
        return service.update(goalId, goalUpdateDto);
    }

    @GetMapping("/{goalId}")
    public GoalDto getById(@PathVariable long goalId) {
        return service.getById(goalId);
    }

    @DeleteMapping("/{goalId}")
    public void delete(@PathVariable long goalId) {
        service.delete(goalId);
    }

    @GetMapping("/search")
    public List<GoalDto> getList(@Valid @ModelAttribute GoalFilterDto goalFilterDto) {
        return service.getByFilters(goalFilterDto);
    }
}
