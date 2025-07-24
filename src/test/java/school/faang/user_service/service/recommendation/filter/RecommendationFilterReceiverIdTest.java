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

        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .receiver(User.builder().id(receiverId).build())
                        .build(),
                Recommendation.builder()
                        .receiver(User.builder().id(2L).build())
                        .build());

        Stream<Recommendation> result = filterReceiverId.filter(recommendations, filterDto);

        List<Recommendation> resultList = result.toList();

        assertEquals(1, resultList.size());
    }

    @Test
    @DisplayName("Фильтр не возвращает рекомендации для указанного получателя если получатель не указан")
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

        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .receiver(User.builder().id(receiverId).build())
                        .build(),
                Recommendation.builder()
                        .receiver(User.builder().id(receiverId).build())
                        .build(),
                Recommendation.builder()
                        .receiver(User.builder().id(2L).build())
                        .build());

        Stream<Recommendation> result = filterReceiverId.filter(recommendations, filterDto);

        List<Recommendation> resultList = result.toList();

        assertEquals(2, resultList.size());
    }
}