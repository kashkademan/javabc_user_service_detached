package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;

import java.util.List;

/**
 * Сервис для управления запросами рекомендаций.
 * Предоставляет методы для создания, фильтрации, получения информации о запросе рекомендации,
 * а также принятия или отклонения запроса с указанием причины отклонения.
 */
public interface RecommendationRequestService {
    /**
     * Создаёт новый запрос рекомендации на основе переданных данных.
     * <p>
     * Условия:
     * <ul>
     *     <li>Запрос рекомендации от одного и того же пользователя к другому можно отправлять не чаще,
     *     чем один раз в 6 месяцев —
     *         в противном случае выбрасывается {@code ForbiddenException}.</li>
     *     <li>Пользователь не может запрашивать рекомендацию сам у себя —
     *         при нарушении выбрасывается {@code ForbiddenException}.</li>
     * </ul>
     *
     * @param recommendationDto объект {@link CreateRecommendationRequestDto}, содержащий данные для создания запроса.
     * @return объект {@link RecommendationRequestDto}, представляющий созданный запрос.
     */
    RecommendationRequestDto create(CreateRecommendationRequestDto recommendationDto);

    /**
     * Возвращает список запросов рекомендаций, удовлетворяющих заданным фильтрам.
     *
     * @param filters объект {@link RecommendationRequestFilterDto}, содержащий данные для фильтрации.
     * @return список объектов {@link RecommendationRequestDto}, представляющих запросы рекомендаций.
     */
    List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filters);

    /**
     * Возвращает информацию о запросе рекомендации по его идентификатору.
     * <p>
     * Если запрос с указанным идентификатором не найден,
     * выбрасывается {@code EntityNotFoundException}.
     *
     * @param id идентификатор запроса.
     * @return объект {@link RecommendationRequestDto}, содержащий данные запроса рекомендации.
     */
    RecommendationRequestDto getById(long id);

    /**
     * Принимает запрос на рекомендацию.
     * <p>
     * Условия:
     * <ul>
     *     <li>Запрос может принять только тот пользователь, кому он был адресован —
     *         в противном случае выбрасывается {@code ForbiddenException}.</li>
     *     <li>Принять запрос можно только в том случае, если он находится в статусе PENDING —
     *         при нарушении выбрасывается {@code ForbiddenException}.</li>
     * </ul>
     *
     * @param id идентификатор запроса.
     */
    void accept(long id);

    /**
     * Отклоняет запрос на рекомендацию.
     * <p>
     * Условия:
     * <ul>
     *     <li>Запрос может отклонить только тот пользователь, кому он был адресован —
     *         в противном случае выбрасывается {@code ForbiddenException}.</li>
     *     <li>Отклонить запрос можно только в том случае, если он находится в статусе PENDING —
     *         при нарушении выбрасывается {@code ForbiddenException}.</li>
     * </ul>
     *
     * @param id идентификатор запроса,
     * @param rejection объект {@link RejectionDto}, содержащий причину отклонения запроса.
     */
    void reject(long id, RejectionDto rejection);
}
