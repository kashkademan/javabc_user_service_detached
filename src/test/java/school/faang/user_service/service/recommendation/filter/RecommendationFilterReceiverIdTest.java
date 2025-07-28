package school.faang.user_service.service.recommendation.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты для {@link RecommendationFilterReceiverId} - фильтра рекомендаций по ID получателя.
 * <p>
 * <ul>
 *      <li>Определение применимости фильтра ({@code isApplicable})</li>
 *      <li>Фильтрация рекомендаций по ID получателя ({@code filter})</li>
 *      <li>Обработка граничных случаев (null значения, пустые коллекции)</li>
 *    </ul>
 *  </p>
 *
 * @author bozya
 * @since 24.07.2025
 */
public class RecommendationFilterReceiverIdTest {
    RecommendationFilterReceiverId filterReceiverId = new RecommendationFilterReceiverId();

    @Test
    @DisplayName("isApplicable возвращает false, если receiverId не указан")
    void testFilterIsApplicableFalse() {
        RecommendationFilterDto filterDto = new RecommendationFilterDto(null, null, null);
        assertFalse(filterReceiverId.isApplicable(filterDto));
    }

    @Test
    @DisplayName("isApplicable возвращает true, если указан receiverId")
    void testFilterIsApplicableTrue() {
        RecommendationFilterDto filterDto = new RecommendationFilterDto(null, null, 1L);
        assertTrue(filterReceiverId.isApplicable(filterDto));
    }

    @Test
    @DisplayName("Фильтр возвращает рекомендации для указанного получателя")
    void testFilterReturnRecommendationsWhenReceiverIdMatches() {
        Long receiverId = 1L;
        RecommendationFilterDto filterDto = new RecommendationFilterDto(null, null, receiverId);

        Recommendation matchingRecommendation = Recommendation.builder()
                .receiver(User.builder().id(receiverId).build())
                .build();

        Recommendation nonMatchingRecommendation = Recommendation.builder()
                .receiver(User.builder().id(2L).build())
                .build();

        Stream<Recommendation> recommendations = Stream.of(
                matchingRecommendation,
                nonMatchingRecommendation);

        Stream<Recommendation> result = filterReceiverId.filter(recommendations, filterDto);
        List<Recommendation> resultList = result.toList();

        assertEquals(1, resultList.size(), "Должна вернуться ровно одна рекомендация");
        assertSame(matchingRecommendation, resultList.get(0),
                "Должна вернуться именно та рекомендация, которую мы передали");
        assertEquals(receiverId, resultList.get(0).getReceiver().getId(),
                "ID получателя должно совпадать с ожидаемым");
    }

    @Test
    @DisplayName("Фильтр не возвращает рекомендации для указанного получателя" +
            " если получатель не указан и возвращает пустой Stream<Recommendation>")
    void testFilterNotReturnRecommendationWhenReceiverIsNull() {
        RecommendationFilterDto filterDto = new RecommendationFilterDto(null, null, null);

        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .receiver(null)
                        .build(),
                Recommendation.builder()
                        .receiver(User.builder().id(2L).build())
                        .build());

        Stream<Recommendation> result = filterReceiverId.filter(recommendations, filterDto);

        List<Recommendation> resultList = result.toList();

        assertEquals(0, resultList.size());
    }

    @Test
    @DisplayName("Фильтр возвращает более 1 рекомендации для указанного получателя")
    void testFilterReturnMoreThanOneRecommendations() {
        Long receiverId = 1L;
        RecommendationFilterDto filterDto = new RecommendationFilterDto(null, null, receiverId);

        Recommendation firstMatching = Recommendation.builder()
                .receiver(User.builder().id(receiverId).build())
                .build();

        Recommendation secondMatching = Recommendation.builder()
                .receiver(User.builder().id(receiverId).build())
                .build();

        Recommendation nonMatching = Recommendation.builder()
                .receiver(User.builder().id(2L).build())
                .build();

        Stream<Recommendation> recommendations = Stream.of(firstMatching, secondMatching, nonMatching);

        Stream<Recommendation> result = filterReceiverId.filter(recommendations, filterDto);
        List<Recommendation> resultList = result.toList();

        assertEquals(2, resultList.size(), "Должно вернуться 2 рекомендации");
        assertTrue(resultList.stream().allMatch(r -> r.getReceiver().getId().equals(receiverId)),
                "Все рекомендации должны быть для указанного получателя");
        assertTrue(resultList.contains(firstMatching), "Первая рекомендация должна быть в результате");
        assertTrue(resultList.contains(secondMatching), "Вторая рекомендация должна быть в результате");
    }
}