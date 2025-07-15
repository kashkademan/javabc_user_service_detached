package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.GoalInvitationViewDto;
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
import school.faang.user_service.service.filter.FilterService;

import java.util.List;

/**
 * Реализация сервиса управления приглашениями к целям.
 * <p>
 * Содержит логику создания приглашений, принятия и отклонения приглашений,
 * а также проверку прав доступа и валидацию данных.
 * </p>*
 *
 * @author Myrza
 * @since 08.07.2025
 */
@Service
@RequiredArgsConstructor
public class GoalInvitationServiceImpl implements GoalInvitationService {
    private static final String CANNOT_INVITE_YOURSELF = "you can't send an invitation to yourself";
    private static final String USER_HAS_NO_ACCESS_TO_GOAL = "the user does no access to the provided goal";
    private static final String USER_HAS_NO_ACCESS_TO_INVITATION = "the user does no access to the provided invitation";
    private static final String INVITED_USER_ALREADY_PARTICIPANT = "the invited user is already member in the goal";
    private static final String INVITATION_PROCESSED = "invitation has been processed";
    private final UserContext userContext;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final FilterService<GoalInvitation, GoalInvitationFilterDto> filterService;

    /**
     * Создает новое приглашение на участие в цели.
     * <p>
     * Проверяет, что пользователь не может пригласить сам себя,
     * что приглашающий действительно является участником цели,
     * и что приглашенный еще не состоит в цели.
     * </p>
     *
     * @param goalId              идентификатор цели
     * @param invitationCreateDto данные для создания приглашения
     * @return созданное приглашение в виде {@link GoalInvitationViewDto}
     * @throws DataValidationException если пользователь пытается пригласить сам себя
     * @throws ForbiddenException      если нет прав на доступ к цели или пользователь уже состоит в ней
     */
    @Override
    @Transactional
    public GoalInvitationViewDto create(long goalId, GoalInvitationCreateDto invitationCreateDto) {
        long inviterUserId = userContext.getUserId();
        long invitedUserId = invitationCreateDto.invitedUserId();
        if (invitedUserId == inviterUserId) {
            throw new DataValidationException(CANNOT_INVITE_YOURSELF);
        }

        if (!goalRepository.isUserMember(goalId, inviterUserId)) {
            throw new ForbiddenException(USER_HAS_NO_ACCESS_TO_GOAL);
        }
        if (goalRepository.isUserMember(goalId, invitedUserId)) {
            throw new ForbiddenException(INVITED_USER_ALREADY_PARTICIPANT);
        }
        GoalInvitation invitation = new GoalInvitation();
        User inviter = userRepository.getByIdOrThrow(inviterUserId);
        invitation.setInviter(inviter);
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        User invited = userRepository.getByIdOrThrow(invitedUserId);
        invitation.setInvited(invited);
        invitation.setGoal(goal);
        invitation.setStatus(RequestStatus.PENDING);
        invitation = goalInvitationRepository.save(invitation);
        return goalInvitationMapper.toViewDto(invitation);
    }

    /**
     * Принимает приглашение по его идентификатору.
     * <p>
     * Меняет статус приглашения на ACCEPTED после проверки прав пользователя.
     * </p>
     *
     * @param invitationId идентификатор приглашения
     * @throws ForbiddenException если пользователь не является приглашенным
     *                            или приглашение уже обработано
     */
    @Override
    public void accept(long invitationId) {
        updateStatus(invitationId, RequestStatus.ACCEPTED);
    }

    /**
     * Отклоняет приглашение по его идентификатору.
     * <p>
     * Меняет статус приглашения на REJECTED после проверки прав пользователя.
     * </p>
     *
     * @param invitationId идентификатор приглашения
     * @throws ForbiddenException если пользователь не является приглашенным
     *                            или приглашение уже обработано
     */
    @Override
    public void reject(long invitationId) {
        updateStatus(invitationId, RequestStatus.REJECTED);
    }

    /**
     * Вспомогательный метод для обновления статуса приглашения.
     * Проверяет права пользователя и текущий статус приглашения.
     *
     * @param invitationId идентификатор приглашения
     * @param status       новый статус
     * @throws ForbiddenException если пользователь не имеет доступа или приглашение уже обработано
     */
    @Transactional
    private void updateStatus(long invitationId, RequestStatus status) {
        long userId = userContext.getUserId();
        GoalInvitation invitation = goalInvitationRepository.getByIdOrThrow(invitationId);
        if (invitation.getInvited().getId() != userId) {
            throw new ForbiddenException(USER_HAS_NO_ACCESS_TO_INVITATION);
        }
        if (!invitation.getStatus().equals(RequestStatus.PENDING)) {
            throw new ForbiddenException(INVITATION_PROCESSED);
        }
        Goal goal = invitation.getGoal();
        if (goalRepository.isUserMember(goal.getId(), userId)) {
            throw new ForbiddenException(INVITED_USER_ALREADY_PARTICIPANT);
        }
        if (RequestStatus.ACCEPTED.equals(status)) {
            User user = userRepository.getByIdOrThrow(userId);
            goal.getUsers().add(user);
            goalRepository.save(goal);
        }
        invitation.setStatus(status);
        goalInvitationRepository.save(invitation);
    }

    private void validateStatusChange(RequestStatus oldStatus) {
        if (!oldStatus.equals(RequestStatus.PENDING)) {
            throw new ForbiddenException(INVITATION_PROCESSED);
        }
    }

    @Override
    @Transactional
    public List<GoalInvitationViewDto> getByFilters(GoalInvitationFilterDto dto) {
        List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll();
        goalInvitations = filterService.getFilteredList(goalInvitations, dto);
        return goalInvitations.stream()
                .map(goalInvitationMapper::toViewDto)
                .toList();
    }
}
