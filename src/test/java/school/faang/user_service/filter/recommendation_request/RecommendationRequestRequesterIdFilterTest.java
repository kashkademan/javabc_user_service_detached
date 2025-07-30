package school.faang.user_service.filter.recommendation_request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestRequesterIdFilterTest {
    RecommendationRequestRequesterIdFilter requesterIdFilter = new RecommendationRequestRequesterIdFilter();

    @Test
    @DisplayName("RequesterId Filter is applicable")
    void isApplicableTrue() {
        boolean applicable = requesterIdFilter.isApplicable(new RecommendationRequestFilterDto(
                1L,
                null,
                null,
                null));

        assertTrue(applicable);
    }

    @Test
    @DisplayName("RequesterId Filter is not applicable")
    void testIsApplicableFalse() {
        boolean applicable = requesterIdFilter.isApplicable(new RecommendationRequestFilterDto(
                null,
                null,
                null,
                null));

        assertFalse(applicable);
    }

    @Test
    @DisplayName("RequesterId Filter applies correctly")
    void testApply() {
        Stream<RecommendationRequest> recommendationRequestStream = Stream.of(
                RecommendationRequest.builder()
                        .requester(User.builder()
                                .id(3L)
                                .build())
                        .build(),
                RecommendationRequest.builder()
                        .requester(User.builder()
                                .id(2L)
                                .build())
                        .build()
        );

        Stream<RecommendationRequest> recommendationRequests = requesterIdFilter.apply(recommendationRequestStream,
                new RecommendationRequestFilterDto(
                        3L,
                        null,
                        null,
                        null
                ));

        List<RecommendationRequest> recommendationRequestList = recommendationRequests.toList();

        assertEquals(1, recommendationRequestList.size());
        assertEquals(3L, recommendationRequestList.get(0).getRequester().getId());
    }
}
