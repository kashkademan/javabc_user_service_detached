package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendationContentFilterTest {
    private final RecommendationContentFilter recommendationContentFilter =
            new RecommendationContentFilter();

    @Test
    public void testIsApplicableTrue() {
        RecommendationFilterDto filter =
                new RecommendationFilterDto("Some content", null, null);
        boolean result = recommendationContentFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        RecommendationFilterDto filter =
                new RecommendationFilterDto(null, null, null);
        boolean result = recommendationContentFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        long originalAuthorId = 1L;
        String searchedContent = "Java";
        RecommendationFilterDto filterDto =
                new RecommendationFilterDto(searchedContent, null, null);
        User firstAuthor = User.builder()
                .id(originalAuthorId)
                .build();
        User secondAuthor = User.builder()
                .id(2L)
                .build();
        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .author(firstAuthor)
                        .content("Some Java-related content")
                        .build(),
                Recommendation.builder()
                        .author(secondAuthor)
                        .content("Some Python-related content")
                        .build()
        );

        List<Recommendation> filteredRecommendation =
                recommendationContentFilter.apply(recommendations, filterDto).toList();

        assertEquals(1, filteredRecommendation.size());
        assertEquals(originalAuthorId, filteredRecommendation.get(0).getAuthor().getId());
        assertEquals("Some Java-related content", filteredRecommendation.get(0).getContent());
    }

    @Test
    public void testApplyNoMatch() {
        RecommendationFilterDto filterDto =
                new RecommendationFilterDto("Some Java-related content", null, null);
        User firstAuthor = User.builder()
                .id(1L)
                .build();
        User secondAuthor = User.builder()
                .id(2L)
                .build();
        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .author(firstAuthor)
                        .content("C#")
                        .build(),
                Recommendation.builder()
                        .author(secondAuthor)
                        .content("Python")
                        .build()
        );

        List<Recommendation> filteredRecommendation =
                recommendationContentFilter.apply(recommendations, filterDto).toList();

        assertEquals(0, filteredRecommendation.size());
    }
}
