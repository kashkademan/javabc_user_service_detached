package school.faang.user_service.policy.goal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultGoalDeletePolicy implements GoalDeletePolicy {

    private final UserContext userContext;
    private final MentorshipRepository mentorshipRepository;

    @Override
    public void validate(Goal goal) {
        Long currentUserId = userContext.getUserId();
        boolean isMentor = goal.getMentor() != null && goal.getMentor().getId().equals(currentUserId);
        boolean isParticipant = goal.getUsers().stream().map(User::getId).anyMatch(val -> val.equals(currentUserId));
        if (!isMentor && !isParticipant) {
            deny(goal);
        }
    }

    private void deny(Goal goal) {
        String msg = String.format(
                "Cannot update goal. Goal ID: %d, Status: %s, CurrentUserId: %d",
                goal.getId(), goal.getStatus(), userContext.getUserId()
        );
        log.error("AccessDenied: {}", msg);
        throw new IllegalArgumentException(msg);
    }
}

