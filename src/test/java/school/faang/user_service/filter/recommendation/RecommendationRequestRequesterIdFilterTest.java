package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 Тестирует функциональность фильтра {@link RecommendationRequestRequesterIdFilter},
 * который отбирает запросы по id отправителя.
 * <p>
 * Проверяет следующие сценарии:
 * <ul>
 *     <li>Фильтр применятся только когда указан requesterId не null</li>
 *     <li>Корректность фильтрации запросов по requesterId</li>
 *     <li>Обработка граничных случаев (пустой входной список, отсутствие совпадений)</li>
 * </ul>
 * </p>
 *
 * @author Linempy
 * @since 16.07.2025
 */
public class RecommendationRequestRequesterIdFilterTest {

    private final RecommendationRequestRequesterIdFilter filter = new RecommendationRequestRequesterIdFilter();

    @Test
    @DisplayName("Проверяет, что id отправителя равен null")
    public void testIsApplicableWhenRequesterIdNull() {
        boolean result = filter.isApplicable(getFilterDto(null));
        assertFalse(result);
    }

    @Test
    @DisplayName("Проверяет, что id отправителя не null")
    public void testIsApplicableWhenRequesterIdNotNull() {
        boolean result = filter.isApplicable(getFilterDto(2L));
        assertTrue(result);
    }

    @Test
    @DisplayName("Проверяет корректность, когда отфильтрованный список не пуст")
    public void testApplyWhenResultNotEmpty() {
        Long targetId = 1L;
        List<RecommendationRequest> result = prepareData(targetId);
        assertEquals(2, result.size());
        assertEquals(targetId, result.get(0).getRequester().getId());
    }

    @Test
    @DisplayName("Проверяет корректность, когда отфильтрованный список пуст")
    public void testApplyWhenResultIsEmpty() {
        Long targetId = 4L;
        List<RecommendationRequest> result = prepareData(targetId);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Проверяет корректность, когда входной список пуст")
    public void testApplyWhenInputIsEmpty() {
        Long targetId = 1L;
        Stream<RecommendationRequest> requests = Stream.empty();
        RecommendationRequestFilterDto filterDto = getFilterDto(targetId);
        Stream<RecommendationRequest> result = filter.apply(requests, filterDto);
        assertEquals(0, result.count());
    }

    private RecommendationRequestFilterDto getFilterDto(Long id) {
        return new RecommendationRequestFilterDto(
                id,
                1L,
                "message",
                RequestStatus.PENDING);
    }

    private RecommendationRequest getRequest(Long id) {
        User requester = User.builder().id(id).build();
        return RecommendationRequest.builder().requester(requester).build();
    }

    private List<RecommendationRequest> prepareData(Long targetId) {
        Stream<RecommendationRequest> requests = Stream.of(
                getRequest(1L),
                getRequest(2L),
                getRequest(1L),
                getRequest(3L)
        );
        RecommendationRequestFilterDto filterDto = getFilterDto(targetId);

        return filter.apply(requests, filterDto).toList();
    }

}