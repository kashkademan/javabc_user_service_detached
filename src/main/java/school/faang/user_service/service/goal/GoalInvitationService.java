package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final UserService userService;

    public void createInvitation(GoalInvitationDto goalInvitationDto) {
        Long goalId = goalInvitationDto.getGoalId();
        Long inviterId = goalInvitationDto.getInviterId();
        Long invitedUserId = goalInvitationDto.getInvitedUserId();
        if (inviterId == null || invitedUserId == null) {
            log.error("Inviter or invited user doesn't exist. InviterId: {}. InvitedId: {}", inviterId, invitedUserId);
            throw new IllegalArgumentException("There's no such inviter or invited user." +
                    " InviterId:" + inviterId + "InvitedId: " + invitedUserId);
        }
        if (Objects.equals(inviterId, invitedUserId)) {
            log.error("Inviter or invited user doesn't exist. InviterId: {}. InvitedId: {}", inviterId, invitedUserId);
            throw new IllegalArgumentException("Inviter and invited user have the same id. InviterId: " + inviterId
                    + "invitedId: " + invitedUserId);
        }
        if (!userRepository.existsById(inviterId) || !userRepository.existsById(invitedUserId)) {
            log.error("One or both users do not exist in the database. InviterId: {}, InvitedId: {}", inviterId, invitedUserId);
            throw new IllegalArgumentException("One or both users do not exist." +
                    " InviterId: " + inviterId + ", InvitedId: " + invitedUserId);
        }

        Goal goal = goalService.getGoalOrThrow(goalId);
        User inviter = userService.getUserById(inviterId);
        User invited = userService.getUserById(invitedUserId);

        GoalInvitation invitation = new GoalInvitation();
        invitation.setGoal(goal);
        invitation.setInviter(inviter);
        invitation.setInvited(invited);
        invitation.setStatus(RequestStatus.PENDING);

        goalInvitationRepository.save(invitation);
        log.info("Invitation successfully created: goalId={}, inviterId={}, invitedUserId={}", goalId, inviterId, invitedUserId);
    }
}
