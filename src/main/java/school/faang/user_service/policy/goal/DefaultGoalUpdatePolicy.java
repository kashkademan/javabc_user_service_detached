package school.faang.user_service.policy.goal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultGoalUpdatePolicy implements GoalUpdatePolicy {

    private final UserContext userContext;
    private final MentorshipRepository mentorshipRepository;

    @Override
    public void validate(UpdateGoalDto dto, Goal goal) {
        long currentUserId = userContext.getUserId();

        boolean isGoalCompleted = goal.getStatus() == GoalStatus.COMPLETED;
        if (isGoalCompleted) {
            deny(dto, goal, currentUserId);
        }

        boolean isMentor = goal.getMentor() != null
                && goal.getMentor().getId() == currentUserId;
        boolean isParticipant = goal.getUsers() != null
                && goal.getUsers().stream()
                .anyMatch(user -> user.getId() == currentUserId);

        if (!isMentor && !isParticipant) {
            deny(dto, goal, currentUserId);
        }
    }

    private void deny(UpdateGoalDto dto, Goal goal, long currentUserId) {
        String msg = String.format(
                "Cannot update goal. Goal ID: %d, Status: %s, CurrentUserId: %d, DTO : %s",
                goal.getId(), goal.getStatus(), currentUserId, dto
        );
        log.error("AccessDenied: {}", msg);
        throw new IllegalArgumentException(msg);
    }
}

