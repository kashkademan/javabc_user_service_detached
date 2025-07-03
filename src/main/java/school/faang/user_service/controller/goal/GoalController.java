package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.exception.AccessDeniedException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.service.goal.GoalService;

@Slf4j
@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/create")
    public GoalDto create(@RequestBody CreateGoalDto createGoalDto) {
        try {
            return goalService.create(createGoalDto);
        } catch (DataValidationException | IllegalArgumentException dataValidationE) {
            log.info("api/goal/create : {}", dataValidationE.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("api/goal/create error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{goalId}/update")
    public GoalDto update(@PathVariable long goalId, @RequestBody UpdateGoalDto updateGoalDto) {
        try {
            return goalService.update(goalId, updateGoalDto);
        } catch (DataValidationException | IllegalArgumentException dataValidationE) {
            log.info("api/goal/{}/update : {}", goalId, dataValidationE.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException accessDeniedE) {
            log.info("api/goal/{}/update : {}", goalId, accessDeniedE.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } catch (IllegalStateException illegalStateE) {
            log.info("api/goal/{}/update : {}", goalId, illegalStateE.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("api/goal/{}/update error: {}", goalId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{goalId}")
    public GoalDto getById(@PathVariable long goalId) {
        try {
            return goalService.getById(goalId);
        } catch (EntityNotFoundException entityNotFoundE) {
            log.info("api/goal/{} : {}", goalId, entityNotFoundE.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found with ID: " + goalId);
        } catch (Exception e) {
            log.error("api/goal/{} error: {}", goalId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
