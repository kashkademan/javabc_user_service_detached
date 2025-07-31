package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.messaging.publishers.GoalAttachedMessagePublisher;
import school.faang.user_service.messaging.publishers.GoalCompletedMessagePublisher;
import school.faang.user_service.service.GoalService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    private final GoalCompletedMessagePublisher goalCompletedMessagePublisher;
    private final GoalAttachedMessagePublisher goalAttachedMessagePublisher;

    @PostMapping("/{userId}/create")
    public GoalDto createGoal(@PathVariable Long userId, @Valid @RequestBody GoalDto goalDto) {
        if (goalDto.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title");
        GoalDto created = goalService.createGoal(userId, goalDto);
        log.info("Goal was created for user {}. Id {}, title {}", userId, created.getId(), created.getTitle());
        return created;
    }

    @PutMapping("/{goalId}/update")
    public GoalDto updateGoal(@PathVariable Long goalId, @Valid @RequestBody GoalDto goalDto) {
        if (goalDto.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title");
        GoalDto updated = goalService.updateGoal(goalId, goalDto);
        log.info("Goal id {} was updated", updated.getId());
        return updated;
    }

    @DeleteMapping("/{goalId}")
    public GoalDto deleteGoal(@PathVariable Long goalId) {
        GoalDto deleted = goalService.deleteGoal(goalId);
        log.info("Goal was deleted, id {}, title {}", deleted.getId(), deleted.getTitle());
        return deleted;
    }

    @PostMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable Long goalId, @Valid @RequestBody GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    @PostMapping("/ofuser/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, @Valid @RequestBody GoalFilterDto filter) {
        return goalService.findGoalsByUserId(userId, filter);
    }

    @GetMapping("/complte-test")
    private void sendFakeGoalComplete() {
        Goal goal = new Goal();
        goal.setId(108L);
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(3L);
        User user3 = new User();
        user3.setId(2L);
        goal.setUsers(List.of(user1, user2, user3));
        goal.setTitle("EXTREMELY IMPORTANT GOAL");
        goalCompletedMessagePublisher.publishMessage(goal);
    }

    @GetMapping("/attache-test")
    private void sendFakeGoalAttached() {
        Goal goal = new Goal();
        goal.setId(111L);
        goal.setTitle("EXTREMELY ATTRACTIVE GOAL");
        Long userId = 3L;
        goalAttachedMessagePublisher.createAndPublishMessage(goal, userId);
    }
}
