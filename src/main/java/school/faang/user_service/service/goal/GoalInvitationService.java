package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.GoalInvitationViewDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;

import java.util.List;

/**
 * Сервис для управления приглашениями к целям.
 * <p>
 * Предоставляет методы для создания, принятия, отклонения и фильтрации приглашений.
 * </p>
 *
 * @author Myrza
 * @since 07.07.2025
 */
public interface GoalInvitationService {
    /**
     * Создает новое приглашение к цели.
     *
     * @param goalId        идентификатор цели
     * @param invitationCreateDto данные для создания приглашения
     * @return созданный объект {@link GoalInvitationViewDto}
     */
    GoalInvitationViewDto create(long goalId, GoalInvitationCreateDto invitationCreateDto);

    /**
     * Принимает приглашение по его идентификатору.
     *
     * @param invitationId идентификатор приглашения
     */
    void accept(long invitationId);

    /**
     * Отклоняет приглашение по его идентификатору.
     *
     * @param invitationId идентификатор приглашения
     */
    void reject(long invitationId);

    /**
     * Получает список приглашений, отфильтрованных по заданным критериям.
     *
     * @param filters параметры фильтрации
     * @return список подходящих приглашений в виде {@link GoalInvitationViewDto}
     */
    List<GoalInvitationViewDto> getByFilters(GoalInvitationFilterDto filters);
}
