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
 * RecommendationFilterAuthorIdTest — тест фильтра по автору.
 * <p>
 * </p>*
 *
 * @author bozya
 * @since 23.07.2025
 */
public class RecommendationFilterAuthorIdTest {
    RecommendationFilterAuthorId recommendationFilter = new RecommendationFilterAuthorId();

    @Test
    @DisplayName("isApplicable возвращает false, когда authorId не указан")
    void testFilterIsApplicableReturnFalseWhenAuthorIsNull() {
        RecommendationFilterDto filterDto = new RecommendationFilterDto("Привет", null, 2L);
        assertFalse(recommendationFilter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("isApplicable возвращает true, когда authorId указан")
    void testFilterIsApplicableReturnTrueWhenAuthorIsNotNull() {
        RecommendationFilterDto filterDto = new RecommendationFilterDto("Привет", 1L, 2L);
        assertTrue(recommendationFilter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("filter возвращает только рекомендации от указанного автора")
    void testFilterReturnOnlyAuthorId() {
        Long authorId = 1L;

        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .author(User.builder().id(authorId).build())
                        .build(),
                Recommendation.builder()
                        .author(User.builder().id(2L).build())
                        .build(),
                Recommendation.builder()
                        .author(User.builder().id(authorId).build())
                        .build());

        RecommendationFilterDto filterDto = new RecommendationFilterDto("Привет", authorId, null);

        Stream<Recommendation> result = recommendationFilter.filter(recommendations, filterDto);

        Stream<Recommendation> expectedRecommendations = Stream.of(
                Recommendation.builder()
                        .author(User.builder().id(authorId).build())
                        .build(),
                Recommendation.builder()
                        .author(User.builder().id(authorId).build())
                        .build());



        List<Recommendation> resultList = result.toList();
        List<Recommendation> expectedList = expectedRecommendations.toList();

        System.out.println(resultList);

        assertEquals(resultList, expectedList);
        assertEquals(2, resultList.size());
    }

    @Test
    @DisplayName("filter возвращает пустой результат, когда нет рекомендаций от указанного автора")
    void testFilterReturnEmptyStreamWhenAuthorNotFound() {
        Long authorId = 1L;

        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .author(User.builder().id(authorId).build())
                        .build(),
                Recommendation.builder()
                        .author(User.builder().id(2L).build())
                        .build(),
                Recommendation.builder()
                        .author(User.builder().id(authorId).build())
                        .build());

        RecommendationFilterDto filterDto = new RecommendationFilterDto("Привет", 3L, null);

        Stream<Recommendation> result = recommendationFilter.filter(recommendations, filterDto);

        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("filter возвращает пустой результат для пустого списка рекомендаций")
    void testFilterReturnEmptyStreamWhenInputIsEmpty() {
        Long authorId = 1L;

        Stream<Recommendation> recommendations = Stream.empty();

        RecommendationFilterDto filterDto = new RecommendationFilterDto("Привет", 3L, null);

        Stream<Recommendation> result = recommendationFilter.filter(recommendations, filterDto);

        assertEquals(0, result.count());
    }
}