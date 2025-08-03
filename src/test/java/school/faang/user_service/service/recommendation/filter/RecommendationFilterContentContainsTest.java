package school.faang.user_service.service.recommendation.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RecommendationFilterContentContainsTest — тестирование класса {@link RecommendationFilterContentContains}.
 *
 * @author bozya
 * @since 24.07.2025
 */
public class RecommendationFilterContentContainsTest {
    RecommendationFilterContentContains recommendationFilter = new RecommendationFilterContentContains();

    @Test
    @DisplayName("isApplicable возвращает True когда contentContains не пустой")
    void testFilterContentContainsIsApplicableTrue() {
        RecommendationFilterDto filterDto = new RecommendationFilterDto("Привет", null, null);
        assertTrue(recommendationFilter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("isApplicable возвращает False когда contentContains пуст")
    void testFilterContentContainsIsApplicableBlankFalse() {
        RecommendationFilterDto filterDto = new RecommendationFilterDto("", null, null);
        assertFalse(recommendationFilter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("isApplicable возвращает False когда contentContains null")
    void testFilterContentContainsIsApplicableNullFalse() {
        RecommendationFilterDto filterDto = new RecommendationFilterDto(null, null, null);
        assertFalse(recommendationFilter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("filter возвращает рекомендации, содержащие искомую подстроку")
    void testFilterReturnRecommendationContainingSubstring() {
        String filterString = "привет";

        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .content("Привет, все хорошо")
                        .build(),
                Recommendation.builder()
                        .content("все плохо")
                        .build());

        RecommendationFilterDto filterDto = new RecommendationFilterDto(filterString, null, null);

        Stream<Recommendation> result = recommendationFilter.filter(recommendations, filterDto);

        List<Recommendation> resultList = result.toList();

        assertEquals(1, resultList.size());
    }

    @Test
    @DisplayName("проверка как фильтр обрабатывает рекомендации с null-значением в поле content"
            + " и возвращает пустой Stream<Recommendation>")
    void testFilterReturnEmptyStreamWhenContentIsNull() {
        String filterString = "Привет";

        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .content(null)
                        .build(),
                Recommendation.builder()
                        .content("все плохо")
                        .build());

        RecommendationFilterDto filterDto = new RecommendationFilterDto(filterString, null, null);

        Stream<Recommendation> result = recommendationFilter.filter(recommendations, filterDto);

        List<Recommendation> resultList = result.toList();

        assertEquals(0, resultList.size());
    }
}