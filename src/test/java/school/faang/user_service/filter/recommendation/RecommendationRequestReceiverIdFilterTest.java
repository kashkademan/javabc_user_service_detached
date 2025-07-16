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
 * Тестирует функциональность фильтра {@link RecommendationRequestReceiverIdFilter},
 * который отбирает запросы по id получателя.
 * <p>
 * Проверяет следующие сценарии:
 * <ul>
 *     <li>Фильтр применятся только когда указан receiverId</li>
 *     <li>Корректность фильтрации запросов по receiverId</li>
 *     <li>Обработка граничных случаев (пустой входной список, отсутствие совпадений)</li>
 * </ul>
 * </p>
 *
 * @author Linempy
 * @since 15.07.2025
 */
public class RecommendationRequestReceiverIdFilterTest {

    private final RecommendationRequestReceiverIdFilter filter = new RecommendationRequestReceiverIdFilter();

    @Test
    @DisplayName("Проверяет, что id получателя равен null")
    public void testIsApplicableWhenReceiverIdIsNull() {
        boolean result = filter.isApplicable(getFilterDto(null));
        assertFalse(result);
    }

    @Test
    @DisplayName("Проверяет, что id получателя не null")
    public void testIsApplicableWhenReceiverIdIsNotNull() {
        boolean result = filter.isApplicable(getFilterDto(2L));
        assertTrue(result);
    }

    @Test
    @DisplayName("Проверяет корректность, когда отфильтрованный список не пуст")
    public void testApplyWhenResultIsNotEmpty() {
        Long targetId = 1L;
        List<RecommendationRequest> result = prepareData(targetId);
        assertEquals(2, result.size());
        assertEquals(targetId, result.get(0).getReceiver().getId());
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
                1L,
                id,
                "message",
                RequestStatus.PENDING);
    }

    private RecommendationRequest getRequest(Long id) {
        User receiver = User.builder().id(id).build();
        return RecommendationRequest.builder().receiver(receiver).build();
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