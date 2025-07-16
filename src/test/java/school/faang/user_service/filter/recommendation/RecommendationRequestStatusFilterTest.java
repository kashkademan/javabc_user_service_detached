package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестирует {@link RecommendationRequestStatusFilter} - фильтр запросов рекомендаций
 * по статусу
 * <p>
 * Проверяет следующие сценарии:
 * <ul>
 *     <li>Фильтр применятся только когда указан status не null</li>
 *     <li>Корректность фильтрации запросов по status</li>
 *     <li>Обработка граничных случаев (пустой входящий список, отсутствие совпадений)</li>
 * </ul>
 * </p>
 *
 * @author Linempy
 * @since 16.07.2025
 */
public class RecommendationRequestStatusFilterTest {

    private final RecommendationRequestStatusFilter filter = new RecommendationRequestStatusFilter();

    @Test
    @DisplayName("Проверяет, что метод должен вернуть false, если status является null")
    public void testIsApplicableWhenStatusIsNull() {
        RecommendationRequestFilterDto dto = getFilterDto(null);
        boolean result = filter.isApplicable(dto);
        assertFalse(result);
    }

    @Test
    @DisplayName("Проверяет, что метод должен вернуть true, если status не null")
    public void testIsApplicableWhenStatusIsNotNull() {
        RecommendationRequestFilterDto dto = getFilterDto(RequestStatus.PENDING);
        boolean result = filter.isApplicable(dto);
        assertTrue(result);
    }

    @Test
    @DisplayName("Должен вернуть запросы, которые совпадают со статусом")
    public void testApplyWhenExistMatch() {
        RequestStatus targetStatus = RequestStatus.PENDING;
        List<RecommendationRequest> filteredRequests = prepareData(targetStatus);
        assertEquals(2, filteredRequests.size());
        assertTrue(filteredRequests.stream()
                .allMatch(request -> request.getStatus() == targetStatus));
    }

    @Test
    @DisplayName("Проверяем, что если совпадений нет, то возвращает пустой список")
    public void testApplyWhenNotMatch() {
        RequestStatus targetStatus = RequestStatus.REJECTED;
        List<RecommendationRequest> filteredRequests = prepareData(targetStatus);
        assertEquals(0, filteredRequests.size());
    }

    @Test
    @DisplayName("Проверка на пустой входной список")
    public void testApplyWhenInputIsEmpty() {
        RequestStatus targetStatus = RequestStatus.ACCEPTED;
        Stream<RecommendationRequest> requests = Stream.empty();
        Stream<RecommendationRequest> filteredRequests = filter.apply(requests, getFilterDto(targetStatus));
        assertEquals(0, filteredRequests.count());
    }

    private RecommendationRequestFilterDto getFilterDto(RequestStatus status) {
        return new RecommendationRequestFilterDto(
                null, null, null, status
        );
    }

    private RecommendationRequest getRequest(RequestStatus status) {
        return RecommendationRequest.builder().status(status).build();
    }

    private List<RecommendationRequest> prepareData(RequestStatus status) {
        Stream<RecommendationRequest> requests = Stream.of(
                getRequest(RequestStatus.PENDING),
                getRequest(null),
                getRequest(RequestStatus.ACCEPTED),
                getRequest(RequestStatus.PENDING)
        );
        RecommendationRequestFilterDto filterDto = getFilterDto(status);

        return filter.apply(requests, filterDto).toList();
    }
}