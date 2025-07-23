package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goal_invitation.GoalInvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final UserService userService;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<GoalInvitationFilter> filters;

    public GoalInvitationDto createInvitation(GoalInvitationCreateDto dto) {
        if (dto.getInviterId() == null || dto.getInvitedUserId() == null) {
            throw new IllegalArgumentException("Inviter and invited user IDs must not be null");
        }
        if (dto.getInviterId().equals(dto.getInvitedUserId())) {
            throw new IllegalArgumentException("Inviter and invited cannot be the same user");
        }
        if (!userRepository.existsById(dto.getInviterId()) || !userRepository.existsById(dto.getInvitedUserId())) {
            throw new IllegalArgumentException("One or both users do not exist");
        }

        Goal goal = goalService.getGoalOrThrow(dto.getGoalId());
        User inviter = userService.getUserById(dto.getInviterId());
        User invited = userService.getUserById(dto.getInvitedUserId());

        GoalInvitation invitation = new GoalInvitation();
        invitation.setGoal(goal);
        invitation.setInviter(inviter);
        invitation.setInvited(invited);
        invitation.setStatus(RequestStatus.PENDING);

        GoalInvitation saved = goalInvitationRepository.save(invitation);
        return goalInvitationMapper.toDto(saved);
    }

    public void acceptGoalInvitation(Long invitationId) {
        GoalInvitation invitation = goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        User invited = invitation.getInvited();
        Goal goal = invitation.getGoal();

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
        goalInvitationRepository.save(invitation);
    }

    public void rejectGoalInvitation(Long invitationId) {
        GoalInvitation invitation = goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        if (invitation.getGoal() == null) {
            throw new IllegalArgumentException("Goal must not be null");
        }
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        Stream<GoalInvitation> stream = goalInvitationRepository.findAll().stream();

        for (GoalInvitationFilter f : filters) {
            if (f.isApplicable(filter)) {
                stream = f.apply(stream, filter);
            }
        }
        return stream.map(goalInvitationMapper::toDto).collect(Collectors.toList());
    }
}
