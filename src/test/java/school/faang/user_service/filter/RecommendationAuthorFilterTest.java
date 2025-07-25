package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RecommendationAuthorFilterTest {
    private final RecommendationAuthorFilter recommendationAuthorFilter =
            new RecommendationAuthorFilter();

    @Test
    public void testIsApplicableTrue() {
        RecommendationFilterDto filter =
                new RecommendationFilterDto(null, 1L, null);
        boolean result = recommendationAuthorFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        RecommendationFilterDto filter =
                new RecommendationFilterDto(null, null, null);
        boolean result = recommendationAuthorFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        long originalAuthorId = 1L;
        RecommendationFilterDto filterDto =
                new RecommendationFilterDto(null, originalAuthorId, null);
        User firstAuthor = User.builder()
                .id(originalAuthorId)
                .build();
        User secondAuthor = User.builder()
                .id(2L)
                .build();
        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .author(firstAuthor)
                        .content("Java")
                        .build(),
                Recommendation.builder()
                        .author(secondAuthor)
                        .content("Python")
                        .build()
        );

        List<Recommendation> filteredRecommendation =
                recommendationAuthorFilter.apply(recommendations, filterDto).toList();

        assertEquals(1, filteredRecommendation.size());
        assertEquals(originalAuthorId, filteredRecommendation.get(0).getAuthor().getId());
        assertEquals("Java", filteredRecommendation.get(0).getContent());
    }

    @Test
    public void testApplyNoMatch() {
        RecommendationFilterDto filterDto =
                new RecommendationFilterDto(null, 3L, null);
        User firstAuthor = User.builder()
                .id(1L)
                .build();
        User secondAuthor = User.builder()
                .id(2L)
                .build();
        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .author(firstAuthor)
                        .content("Java")
                        .build(),
                Recommendation.builder()
                        .author(secondAuthor)
                        .content("Python")
                        .build()
        );

        List<Recommendation> filteredRecommendation =
                recommendationAuthorFilter.apply(recommendations, filterDto).toList();

        assertEquals(0, filteredRecommendation.size());
    }
}
