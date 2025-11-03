package school.faang.user_service.service.goal;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import school.faang.user_service.mapper.CreateGoalInvitationMapper;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final UserContext userContext;
    private final CreateGoalInvitationMapper createGoalInvitationMapper;
    private final ValidationService validationService;

    @Transactional
    @Override
    public GoalInvitationDto create(long goalId, CreateGoalInvitationDto invitationDto) {
        log.info("Creating goal invitation for goalId={}, invitedUserId={}",
                goalId, invitationDto.invitedUserId());
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        validationService.validateCreation(goal, invitationDto);

        GoalInvitation goalInvitation = createGoalInvitationMapper.toGoalInvitation(invitationDto);

        goalInvitation.setInvited(userRepository.getByIdOrThrow(invitationDto.invitedUserId()));
        goalInvitation.setGoal(goal);
        goalInvitation.setStatus(RequestStatus.PENDING);

        List<GoalInvitation> invitations = goal.getInvitations();

        GoalInvitation savedInvitation = goalInvitationRepository.save(goalInvitation);
        log.info("Goal invitation created: id={}, goalId={}, invitedUserId={}",
                goalInvitation.getId(), goal.getId(), goalInvitation.getInvited().getId());
        invitations.add(savedInvitation);

        return goalInvitationMapper.toGoalInvitationDto(goalInvitation);
    }

    @Transactional
    @Override
    public void accept(long invitationId) {
        log.info("Accepting invitation id={} by userId={}", invitationId, userContext.getUserId());
        GoalInvitation invitation = goalInvitationRepository.getByIdOrThrow(invitationId);
        validationService.canAcceptOrReject(invitation.getInvited().getId());
        User user = userRepository.getByIdOrThrow(userContext.getUserId());
        Long goalId = invitation.getGoal().getId();
        validationService.validateAccept(user, goalId);
        invitation.setStatus(RequestStatus.ACCEPTED);
        invitation.getGoal().getUsers().add(user);
        goalInvitationRepository.save(invitation);
        log.info("Invitation id={} accepted by userId={}", invitation.getId(), user.getId());
    }

    @Transactional
    @Override
    public void reject(long invitationId) {
        log.info("Rejecting invitation id={} by userId={}", invitationId, userContext.getUserId());
        GoalInvitation invitation = goalInvitationRepository.getByIdOrThrow(invitationId);
        validationService.canAcceptOrReject(invitation.getInvited().getId());
        invitation.setStatus(RequestStatus.REJECTED);
        goalInvitationRepository.save(invitation);
        log.info("Invitation id={} rejected by userId={}", invitation.getId(), userContext.getUserId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GoalInvitationDto> getByFilters(GoalInvitationFilterDto filters) {
        List<GoalInvitation> invitations = goalInvitationRepository.findAll();
        List<GoalInvitation> filteredInvitation = invitations.stream()
                .filter(goalInvitation -> goalInvitation.getInvited().getId().equals(filters.invitedId()))
                .filter(goalInvitation -> goalInvitation.getStatus().equals(filters.status())).toList();
        log.info("Found {} invitations after filtering", filteredInvitation.size());
        return goalInvitationMapper.toListGoalInvitationDto(filteredInvitation);
    }
}
