package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.goal.GoalService;

@Slf4j
@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/create")
    public GoalDto create(@RequestBody GoalCreateDto goalCreateDto) {
        try {
            return goalService.create(goalCreateDto);
        } catch (DataValidationException | IllegalArgumentException dataValidationE) {
            log.info("api/goal/create : {}", dataValidationE.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("api/goal/create error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{goalId}/update")
    public GoalDto update(@PathVariable long goalId, @RequestBody GoalUpdateDto goalUpdateDto) {
        try {
            return goalService.update(goalId, goalUpdateDto);
        } catch (DataValidationException | IllegalArgumentException dataValidationE) {
            log.info("api/goal/{}/update : {}", goalId, dataValidationE.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException forbiddenE) {
            log.info("api/goal/{}/update : {}", goalId, forbiddenE.getMessage());
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
            log.info("api/goal/{} get : {}", goalId, entityNotFoundE.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found with ID: " + goalId);
        } catch (ForbiddenException forbiddenE) {
            log.info("api/goal/{} get : {}", goalId, forbiddenE.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.info("api/goal/{} error: {}", goalId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{goalId}")
    public void delete(@PathVariable long goalId) {
        try {
            goalService.delete(goalId);
        } catch (IllegalArgumentException illegalArgumentE) {
            log.info("api/goal/{} delete :{}", goalId, illegalArgumentE.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException entityNotFoundE) {
            log.info("api/goal/{} delete : {}", goalId, entityNotFoundE.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found with ID: " + goalId);
        } catch (ForbiddenException forbiddenE) {
            log.info("api/goal/{} delete : {}", goalId, forbiddenE.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error("api/goal/{} error: {}", goalId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
