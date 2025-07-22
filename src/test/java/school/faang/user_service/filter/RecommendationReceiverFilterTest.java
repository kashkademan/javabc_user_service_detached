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

public class RecommendationReceiverFilterTest {
    private final RecommendationReceiverFilter recommendationReceiverFilter =
            new RecommendationReceiverFilter();

    @Test
    public void testIsApplicableTrue() {
        RecommendationFilterDto filter =
                new RecommendationFilterDto(null, null, 1L);
        boolean result = recommendationReceiverFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        RecommendationFilterDto filter =
                new RecommendationFilterDto(null, null, null);
        boolean result = recommendationReceiverFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        long originalReceiverId = 1L;
        RecommendationFilterDto filterDto =
                new RecommendationFilterDto(null, null, originalReceiverId);
        User firstReceiver = User.builder()
                .id(originalReceiverId)
                .build();
        User secondReceiver = User.builder()
                .id(2L)
                .build();
        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .receiver(firstReceiver)
                        .content("Java")
                        .build(),
                Recommendation.builder()
                        .receiver(secondReceiver)
                        .content("Python")
                        .build()
        );

        List<Recommendation> filteredRecommendation =
                recommendationReceiverFilter.apply(recommendations, filterDto).toList();

        assertEquals(1, filteredRecommendation.size());
        assertEquals(originalReceiverId, filteredRecommendation.get(0).getReceiver().getId());
        assertEquals("Java", filteredRecommendation.get(0).getContent());
    }

    @Test
    public void testApplyNoMatch() {
        RecommendationFilterDto filterDto =
                new RecommendationFilterDto(null, null, 3L);
        User firstReceiver = User.builder()
                .id(1L)
                .build();
        User secondReceiver = User.builder()
                .id(2L)
                .build();
        Stream<Recommendation> recommendations = Stream.of(
                Recommendation.builder()
                        .receiver(firstReceiver)
                        .content("Java")
                        .build(),
                Recommendation.builder()
                        .receiver(secondReceiver)
                        .content("Python")
                        .build()
        );

        List<Recommendation> filteredRecommendation =
                recommendationReceiverFilter.apply(recommendations, filterDto).toList();

        assertEquals(0, filteredRecommendation.size());
    }
}
