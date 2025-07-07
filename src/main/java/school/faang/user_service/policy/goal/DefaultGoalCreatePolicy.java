package school.faang.user_service.policy.goal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultGoalCreatePolicy implements GoalCreatePolicy {
    public static final int USER_GOAL_THRESHOLD = 2;

    private final UserContext userContext;
    private final MentorshipRepository mentorshipRepository;
    private final GoalRepository goalRepository;

    @Override
    public void validate(CreateGoalDto dto) {
        long currentUserId = userContext.getUserId();
        boolean isSelf = dto.userIds() != null && dto.userIds().contains(currentUserId);
        Long mentorId = dto.mentorId();
        boolean isMentee = mentorId != null
                           && mentorshipRepository.existsByMentorIdAndMenteeIds(mentorId, dto.userIds());

        if (!isSelf && !isMentee) {
            deny(currentUserId, dto);
        }
        if (!isParticipantsGoalLimitExceeded(dto.userIds())) {
            throw new IllegalArgumentException("У участников цели не может быть более "
                                               + USER_GOAL_THRESHOLD + " активных целей");
        }
    }

    private void deny(long userId, CreateGoalDto dto) {
        String msg = String.format("User %d cannot create goal for userIds=%s", userId, dto.userIds());
        log.error("AccessDenied: {}", msg);
        throw new IllegalArgumentException(msg);
    }

    private boolean isParticipantsGoalLimitExceeded(List<Long> userIds) {
        if (userIds == null) {
            return false;
        }
        return userIds.size() == goalRepository.countUsersExceedingGoals(
                userIds,
                GoalStatus.ACTIVE,
                USER_GOAL_THRESHOLD);
    }
}

