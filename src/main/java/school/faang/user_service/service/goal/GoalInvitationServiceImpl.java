package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.UserServiceException;
import school.faang.user_service.filter.invitation.InvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.messaging.publishers.GoalAttachedMessagePublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {

    @Value("${logic.constants.max_active_goals}")
    private int maximumAllowedActiveGoals;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<InvitationFilter> invitationFilters;
    private final GoalAttachedMessagePublisher goalAttachedMessagePublisher;

    @Override
    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        Long inviterId = goalInvitationDto.getInviterId();
        Long invitedUserId = goalInvitationDto.getInvitedUserId();
        if (invitedUserId.equals(inviterId))
            throw new IllegalArgumentException("Inviter and Invited IDs are the same");

        GoalInvitation goalInvitation = goalInvitationMapper.toGoalInvitation(goalInvitationDto);

        Long goalId = goalInvitationDto.getGoalId();
        goalInvitation.setGoal(goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal id: " + goalId)));
        goalInvitation.setInviter(userRepository.findById(inviterId)
                .orElseThrow(() -> new EntityNotFoundException("User id: " + inviterId)));
        goalInvitation.setInvited(userRepository.findById(invitedUserId)
                .orElseThrow(() -> new EntityNotFoundException("User id: " + invitedUserId)));

        goalInvitation.setStatus(RequestStatus.PENDING);

        GoalInvitation created = goalInvitationRepository.saveAndFlush(goalInvitation);
        return goalInvitationMapper.toGoalInvitationDto(created);
    }

    @Override
    public void acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invitation ID: " + id));

        Goal goal = goalInvitation.getGoal();
        User invited = goalInvitation.getInvited();

        if (goal == null) {
            throw new IllegalStateException("No existing goal in invitation with id " + id);
        }

        if (isUserAlreadyWorksOnGoal(goal, invited)) {
            throw new UnsupportedOperationException(
                    String.format("Invited user id: %d already works on goal %s", invited.getId(), goal.getTitle())
            );
        }

        if (isMaximumAllowedActiveGoalsReachedForUser(invited)) {
            throw new UserServiceException(
                    String.format("User id: %d has Maximum allowed active goals - %d",
                            invited.getId(),
                            maximumAllowedActiveGoals));
        }

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitationRepository.saveAndFlush(goalInvitation);
        goal.getUsers().add(invited);
        invited.getGoals().add(goal);
        goalRepository.saveAndFlush(goal);
        userRepository.saveAndFlush(invited);

        goalAttachedMessagePublisher.createAndPublishMessage(goal, invited.getId());
    }

    @Override
    public void rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invitation ID: " + id));
        if (goalInvitation.getGoal() != null) {
            goalInvitation.setStatus(RequestStatus.REJECTED);
            goalInvitationRepository.saveAndFlush(goalInvitation);
        }
    }

    @Override
    public List<GoalInvitationDto> getInvitations(InvitationFilterIDto filter) {
        List<InvitationFilter> applicableFilters = invitationFilters.stream()
                .filter(invitationFilter -> invitationFilter.isApplicable(filter))
                .toList();

        List<GoalInvitation> filteredGoalInvitations = goalInvitationRepository.findAll().stream()
                .filter(goalInvitation -> applicableFilters.stream()
                        .allMatch(invitationFilter -> invitationFilter.doFilter(goalInvitation, filter)))
                .toList();

        return goalInvitationMapper.toDtos(filteredGoalInvitations);
    }

    private boolean isMaximumAllowedActiveGoalsReachedForUser(User invited) {
        long activeGoalsOfInvited = invited.getGoals().stream()
                .filter(goal -> GoalStatus.ACTIVE == goal.getStatus())
                .count();
        return activeGoalsOfInvited >= maximumAllowedActiveGoals;
    }

    private boolean isUserAlreadyWorksOnGoal(Goal goal, User invited) {
        return invited.getGoals().stream()
                .anyMatch(goal::equals);
    }
}
