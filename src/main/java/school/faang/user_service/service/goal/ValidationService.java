package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final UserContext userContext;
    public void validateCreation(Goal goal, CreateGoalInvitationDto invitationDto) {
        boolean inviterIsInvitedUser = invitationDto.invitedUserId().equals(userContext.getUserId());
        boolean invitedUserAlreadyWorkOnThisGoal = goal.getUsers().stream()
                .anyMatch(user -> invitationDto.invitedUserId().equals(user.getId()));
        boolean invitedUserIsMentor = goal.getMentor() != null
                && invitationDto.invitedUserId().equals(goal.getMentor().getId());

        if (inviterIsInvitedUser || invitedUserAlreadyWorkOnThisGoal || invitedUserIsMentor) {
            log.warn("User {} cannot be invited to goal {}: already involved",
                    invitationDto.invitedUserId(), goal.getId());
            throw new ForbiddenException("Invited User is already working on this goal!");
        }
    }

    public void validateAccept(User user, long goalId) {
        userContext.getUserId();
        long activeGoals = user.getGoals().stream().filter(goal -> goal.getStatus().equals(GoalStatus.ACTIVE)).count();
        boolean alreadyJoin = user.getGoals().stream().anyMatch(goal -> goal.getId().equals(goalId));

        if (activeGoals >= 3 || alreadyJoin) {
            log.warn("User {} cannot accept goal {}: activeGoals={}, alreadyJoin={}",
                    user.getId(), goalId, activeGoals, alreadyJoin);
            throw new ForbiddenException("User has enough goals!");
        }
    }

    public void canAcceptOrReject(Long invitedUserId) {
        if (!invitedUserId.equals(userContext.getUserId())) {
            log.warn("User {} tried to accept/reject invitation for invitedUserId={}",
                    userContext.getUserId(), invitedUserId);
            throw new ForbiddenException("Accept or reject the invitation can invited user only!");
        }
    }
}
