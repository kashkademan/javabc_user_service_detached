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

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private static final int MAX_ACTIVE_GOALS = 3;
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

    public void acceptGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(goalInvitationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("There is no invitation with id: " + goalInvitationId));
        User user = goalInvitation.getInvited();
        List<Goal> userGoals = user.getGoals();
        Goal goal = goalInvitation.getGoal();
        List<User> goalUsers = goal.getUsers();

        if (userGoals.size() >= MAX_ACTIVE_GOALS) {
            log.error("User with id: {}  already have more than {} active goals", user.getId(), MAX_ACTIVE_GOALS);
            throw new IllegalArgumentException("User already has maximum goals. Maximum goals: " + MAX_ACTIVE_GOALS);
        }
        if (goalUsers.contains(user)) {
            log.error("User with id: {} alredy working on goal with id: {}", user.getId(), goal.getId());
            throw new IllegalArgumentException("User with id = " + user.getId() +
                    " is already working on goal with id: " + goal.getId());
        }
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        user.getGoals().add(goal);
        goal.getUsers().add(user);

        userService.updateUser(user);


    }

    public void rejectGoalInvitation(long goalInvitationId) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(goalInvitationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("There's no invitation with id: " + goalInvitationId));

        if (goalInvitation.getGoal() == null) {
            throw new IllegalArgumentException("Goal for this invitation does not exist. Invitation id: " + goalInvitationId);
        }

        goalInvitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(goalInvitation);

        log.info("Invitation {} was rejected for goal {}", goalInvitationId, goalInvitation.getGoal().getId());
    }
}
