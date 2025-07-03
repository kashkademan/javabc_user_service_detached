package school.faang.user_service.policy.goal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultGoalCreatePolicy implements GoalCreatePolicy {

    private final UserContext userContext;
    private final MentorshipRepository mentorshipRepository;

    @Override
    public void validate(CreateGoalDto dto) {
        long currentUserId = userContext.getUserId();
        boolean isSelf = dto.userIds().contains(currentUserId);
        Long mentorId = dto.mentorId();
        boolean isMentee = mentorId != null
                && mentorshipRepository.existsByMentorIdAndMenteeIds(mentorId, dto.userIds());

        if (!isSelf && !isMentee) {
            deny(currentUserId, dto);
        }
    }

    private void deny(long userId, CreateGoalDto dto) {
        String msg = String.format("User %d cannot create goal for userIds=%s", userId, dto.userIds());
        log.error("AccessDenied: {}", msg);
        throw new IllegalArgumentException(msg);
    }
}

