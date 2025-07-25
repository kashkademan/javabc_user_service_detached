package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreateDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestViewDto;

import java.util.List;

/**
 * Сервис для работы с запросами на менторство.
 * <p>
 * Обеспечивает создание запросов, получение с фильтрацией,
 * а также обработку действий по принятию и отклонению запросов.
 * </p>
 */
public interface MentorshipRequestService {

    /**
     * Создаёт новый запрос на менторство от текущего пользователя к указанному получателю.
     * <p>
     * Проверяет бизнес-правила, такие как запрет отправки запроса самому себе
     * и ограничение по частоте отправки запросов (не чаще одного раза в 3 месяца).
     * </p>
     *
     * @param mentorshipRequestCreateDto DTO с параметрами создания запроса
     * @return DTO созданного запроса с детальной информацией
     */
    MentorshipRequestViewDto create(MentorshipRequestCreateDto mentorshipRequestCreateDto);

    /**
     * Получает список запросов на менторство с возможностью фильтрации по отправителю, получателю и статусу.
     * <p>
     * Для успешного запроса обязательно должен быть указан либо идентификатор отправителя, либо получателя.
     * </p>
     *
     * @param filter DTO с параметрами фильтрации (requesterId, receiverId, status)
     * @return список DTO запросов, соответствующих фильтру
     */
    List<MentorshipRequestViewDto> getByFilters(MentorshipRequestFilterDto filter);

    /**
     * Принимает запрос на менторство.
     * <p>
     * Выполняет проверку, что запрос существует, и что текущий пользователь является получателем.
     * Устанавливает статус запроса в ACCEPTED и создаёт связь ментор-менти.
     * </p>
     *
     * @param requestId идентификатор запроса на менторство
     */
    void accept(long requestId);

    /**
     * Отклоняет запрос на менторство с указанием причины.
     * <p>
     * Проверяет, что причина отказа указана и что текущий пользователь является получателем запроса.
     * Обновляет статус запроса на REJECTED и сохраняет причину.
     * </p>
     *
     * @param requestId идентификатор запроса на менторство
     * @param rejectionDto DTO с причиной отказа
     */
    void reject(long requestId, RejectionDto rejectionDto);
}