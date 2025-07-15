package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestViewDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;

/**
 * RecommendationRequestService — интерфейс сервиса для работы с запросами на рекомендацию
 * <p>
 * Предоставляет создание, фильтрацию, получение, принятие и отклонение запросов на рекомендации
 * </p>
 *
 * @author Linempy
 * @since 14.07.2025
 */
public interface RecommendationRequestService {

    /**
     * Создает и сохраняет в базу данных запрос на рекомендацию
     * <p>
     * Проверяет, что получатель не совпадает с отправителем (по id)
     * и что не превышен лимит запросов (cooldown period)
     * </p>
     *
     * @param createDto DTO {@link RecommendationRequestCreateDto} с данными запроса (ID получателя, сообщение, навыки)
     * @return {@link RecommendationRequestViewDto} - DTO с информацией о созданном запросе
     * @throws school.faang.user_service.exception.ForbiddenException если нарушены бизнес-требования
     */
    RecommendationRequestViewDto create(RecommendationRequestCreateDto createDto);

    /**
     * Возвращает список запросов, отфильтрованные по заданным критериям
     *
     * @param filters DTO {@link RecommendationRequestFilterDto} с параметрами фильтрации
     * @return список {@link RecommendationRequestViewDto} DTO запросов, удовлетворяющих критериям
     */
    List<RecommendationRequestViewDto> getByFilters(RecommendationRequestFilterDto filters);

    /**
     * Возвращает DTO {@link RecommendationRequestViewDto} запроса по его идентификатору
     *
     * @param id идентификатор запроса
     * @return {@link RecommendationRequestViewDto}
     */
    RecommendationRequestViewDto getById(long id);

    /**
     * Помечает запрос как принятый {@code ACCEPTED}
     * (см. {@link school.faang.user_service.entity.RequestStatus})
     * <p>
     * Проверяет, что изменить статус может только пользователь, который принимает запрос
     * и что изначальный статус запроса {@code PENDING}
     * </p>
     *
     * @param id идентификатор запроса, для которого меняется статус
     * @throws ForbiddenException если проверки не прошли
     */
    void accept(long id);

    /**
     * Помечает запрос как отклоненный {@code REJECTED}
     * (см. {@link school.faang.user_service.entity.RequestStatus})
     * <p>
     * Проверяет, что изменить статус может только пользователь, который принимает запрос
     * и что изначальный статус запроса {@code PENDING}
     * </p>
     *
     * @param id идентификатор запроса, для которого меняется статус
     * @param rejection DTO {@link RejectionDto} с причинной отклонения
     * @throws ForbiddenException если проверки не прошли
     */
    void reject(long id, RejectionDto rejection);
}
