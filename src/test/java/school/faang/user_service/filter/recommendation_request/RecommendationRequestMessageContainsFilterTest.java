package school.faang.user_service.filter.recommendation_request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestMessageContainsFilterTest {
    private final RecommendationRequestMessageContainsFilter messageContainsFilter =
            new RecommendationRequestMessageContainsFilter();

    @Test
    @DisplayName("Message Contains Filter is applicable")
    void testIsApplicableTrue() {
        boolean applicable = messageContainsFilter.isApplicable(new RecommendationRequestFilterDto(
                null,
                null,
                "message",
                null));

        assertTrue(applicable);
    }

    @Test
    @DisplayName("Message Contains Filter is not applicable")
    void testIsApplicableFalse() {
        boolean applicable = messageContainsFilter.isApplicable(new RecommendationRequestFilterDto(
                null,
                null,
                null,
                null));

        assertFalse(applicable);
    }

    @Test
    @DisplayName("Message Contains Filter applies correctly")
    void testApply() {
        Stream<RecommendationRequest> recommendationRequestStream = Stream.of(
                RecommendationRequest.builder().message("test message").build(),
                RecommendationRequest.builder().message("other message").build(),
                RecommendationRequest.builder().message("").build(),
                RecommendationRequest.builder().message("null").build(),
                RecommendationRequest.builder().message("  ").build(),
                RecommendationRequest.builder().message("test").build());

        Stream<RecommendationRequest> recommendationRequests = messageContainsFilter.apply(recommendationRequestStream,
                new RecommendationRequestFilterDto(
                        null,
                        null,
                        "test",
                        null
                ));

        List<RecommendationRequest> recommendationRequestList = recommendationRequests.toList();

        assertEquals(2, recommendationRequestList.size());
        assertTrue(recommendationRequestList.stream().allMatch(recommendationRequest ->
                recommendationRequest.getMessage().contains("test")));
    }
}
