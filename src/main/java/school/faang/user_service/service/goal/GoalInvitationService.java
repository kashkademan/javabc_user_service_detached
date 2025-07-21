package school.faang.user_service.service.goal;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final UserService userService;
    private final GoalInvitationMapper goalInvitationMapper;

    public void createInvitation(GoalInvitationDto dto) {
        final Long goalId = dto.getGoalId();

        if (dto.getInviterId() == null || dto.getInvitedUserId() == null) {
            throw new IllegalArgumentException("Inviter and invited user IDs must not be null");
        }

        if (dto.getInviterId().equals(dto.getInvitedUserId())) {
            throw new IllegalArgumentException("Inviter and invited cannot be the same user");
        }

        if (!userRepository.existsById(dto.getInviterId())
                || !userRepository.existsById(dto.getInvitedUserId())) {
            throw new IllegalArgumentException("One or both users do not exist");
        }

        final Goal goal = goalService.getGoalOrThrow(goalId);
        final User inviter = userService.getUserById(dto.getInviterId());
        final User invited = userService.getUserById(dto.getInvitedUserId());

        final GoalInvitation invitation = new GoalInvitation();
        invitation.setGoal(goal);
        invitation.setInviter(inviter);
        invitation.setInvited(invited);
        invitation.setStatus(RequestStatus.PENDING);

        goalInvitationRepository.save(invitation);
    }

    public void acceptGoalInvitation(Long invitationId) {
        final GoalInvitation invitation = goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        final User invited = invitation.getInvited();
        final Goal goal = invitation.getGoal();

        if (invited.getGoals().size() >= 3) {
            throw new IllegalArgumentException("User has reached the maximum number of goals");
        }

        if (goal.getUsers().contains(invited)) {
            throw new IllegalArgumentException("User is already in this goal");
        }

        invitation.setStatus(RequestStatus.ACCEPTED);
        invited.getGoals().add(goal);
        goal.getUsers().add(invited);
        userService.updateUser(invited);
    }

    public void rejectGoalInvitation(Long invitationId) {
        final GoalInvitation invitation = goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        if (invitation.getGoal() == null) {
            throw new IllegalArgumentException("Goal must not be null");
        }

        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        return goalInvitationRepository.findAll()
                .stream()
                .filter(invitation -> filter.getInviterId() == null
                        || invitation.getInviter().getId().equals(filter.getInviterId()))
                .filter(invitation -> filter.getInvitedId() == null
                        || invitation.getInvited().getId().equals(filter.getInvitedId()))
                .filter(invitation -> filter.getStatus() == null
                        || invitation.getStatus().equals(filter.getStatus()))
                .map(goalInvitationMapper::toDto)
                .collect(Collectors.toList());
    }
}
