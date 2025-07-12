package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationServiceImpl implements GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserContext userContext;

    @Value("${app.goals.max-active-goals:3}")
    private int maxActiveGoals;

    @Override
    @Transactional
    public GoalInvitationDto create(long goalId, CreateGoalInvitationDto invitationDto) {
        log.info("Creating invitation for goal {} to user {}", goalId, invitationDto.getInvitedUserId());

        if (invitationDto.getInvitedUserId() == null) {
            throw new DataValidationException("invitedUserId must not be null");
        }

        User inviter = userRepository.findById(userContext.getUserId())
                .orElseThrow(() -> new DataValidationException("Inviter not found"));
        User invited = userRepository.findById(invitationDto.getInvitedUserId())
                .orElseThrow(() -> new DataValidationException("Invited user not found"));
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new DataValidationException("Goal not found"));

        if (inviter.getId().equals(invited.getId())) {
            throw new DataValidationException("Cannot invite yourself");
        }

        if (goal.getUsers().contains(invited) ||
                (goal.getMentor() != null && goal.getMentor().equals(invited))) {
            throw new DataValidationException("User is already participating in this goal");
        }

        List<GoalInvitation> existingInvitations = goalInvitationRepository.findAll().stream()
                .filter(inv -> inv.getGoal().getId().equals(goalId) &&
                        inv.getInvited().getId().equals(invited.getId()) &&
                        (inv.getStatus() == RequestStatus.PENDING || inv.getStatus() == RequestStatus.ACCEPTED))
                .toList();

        if (!existingInvitations.isEmpty()) {
            throw new DataValidationException("Active invitation already exists");
        }

        GoalInvitation invitation = new GoalInvitation();
        invitation.setInviter(inviter);
        invitation.setInvited(invited);
        invitation.setGoal(goal);
        invitation.setStatus(RequestStatus.PENDING);

        GoalInvitation saved = goalInvitationRepository.save(invitation);
        log.info("Invitation created successfully with id {}", saved.getId());

        return goalInvitationMapper.toGoalInvitationDto(saved);
    }

    @Override
    @Transactional
    public void accept(long invitationId) {
        log.info("Accepting invitation with id {}", invitationId);

        Long currentUserId = userContext.getUserId();

        GoalInvitation invitation = goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new DataValidationException("Invitation not found"));

        if (!invitation.getInvited().getId().equals(currentUserId)) {
            throw new ForbiddenException("You can only accept invitations addressed to you");
        }

        if (invitation.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Invitation is not pending");
        }

        User invited = invitation.getInvited();
        Goal goal = invitation.getGoal();

        if (goal.getUsers().contains(invited) ||
                (goal.getMentor() != null && goal.getMentor().equals(invited))) {
            throw new DataValidationException("You are already participating in this goal");
        }

        int activeGoalsCount = goalRepository.countActiveGoalsPerUser(invited.getId());
        if (activeGoalsCount >= maxActiveGoals) {
            throw new DataValidationException("You cannot have more than " + maxActiveGoals + " active goals");
        }

        invitation.setStatus(RequestStatus.ACCEPTED);
        goal.getUsers().add(invited);

        goalInvitationRepository.save(invitation);
        goalRepository.save(goal);

        log.info("Invitation {} accepted successfully by user {}", invitationId, currentUserId);
    }

    @Override
    @Transactional
    public void reject(long invitationId) {
        log.info("Rejecting invitation with id {}", invitationId);

        Long currentUserId = userContext.getUserId();

        GoalInvitation invitation = goalInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new DataValidationException("Invitation not found"));

        if (!invitation.getInvited().getId().equals(currentUserId)) {
            throw new ForbiddenException("You can only reject invitations addressed to you");
        }

        if (invitation.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Invitation is not pending");
        }

        invitation.setStatus(RequestStatus.REJECTED);

        goalInvitationRepository.save(invitation);

        log.info("Invitation {} rejected successfully by user {}", invitationId, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalInvitationDto> getByFilters(GoalInvitationFilterDto filters) {
        log.info("Getting invitations by filters: inviterId={}, invitedId={}, status={}",
                filters.inviterId(), filters.invitedId(), filters.status());

        List<GoalInvitation> invitations = goalInvitationRepository.findAll();

        List<GoalInvitation> filteredInvitations = invitations.stream()
                .filter(invitation -> filters.inviterId() == null ||
                        invitation.getInviter().getId().equals(filters.inviterId()))
                .filter(invitation -> filters.invitedId() == null ||
                        invitation.getInvited().getId().equals(filters.invitedId()))
                .filter(invitation -> filters.status() == null ||
                        invitation.getStatus().equals(filters.status()))
                .toList();

        List<GoalInvitationDto> result = filteredInvitations.stream()
                .map(goalInvitationMapper::toGoalInvitationDto)
                .toList();

        log.info("Found {} invitations matching filters", result.size());
        return result;
    }
}