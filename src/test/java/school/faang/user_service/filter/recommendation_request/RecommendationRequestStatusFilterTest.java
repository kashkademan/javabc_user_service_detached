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
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.entity.RequestStatus.PENDING;
import static school.faang.user_service.entity.RequestStatus.REJECTED;

class RecommendationRequestStatusFilterTest {
    private final RecommendationRequestStatusFilter statusFilter = new RecommendationRequestStatusFilter();

    @Test
    @DisplayName("Status Filter is applicable")
    void testIsApplicableTrue() {
        boolean applicable = statusFilter.isApplicable(new RecommendationRequestFilterDto(
                null,
                null,
                null,
                PENDING));

        assertTrue(applicable);
    }

    @Test
    @DisplayName("Status Filter is not applicable")
    void testIsApplicableFalse() {
        boolean applicable = statusFilter.isApplicable(new RecommendationRequestFilterDto(
                null,
                null,
                null,
                null));

        assertFalse(applicable);
    }

    @Test
    @DisplayName("Status Filter applies correctly")
    void testApply() {
        Stream<RecommendationRequest> recommendationRequestStream = Stream.of(
                RecommendationRequest.builder().status(PENDING).build(),
                RecommendationRequest.builder().status(ACCEPTED).build(),
                RecommendationRequest.builder().status(REJECTED).build(),
                RecommendationRequest.builder().status(ACCEPTED).build(),
                RecommendationRequest.builder().status(PENDING).build(),
                RecommendationRequest.builder().status(ACCEPTED).build());

        Stream<RecommendationRequest> recommendationRequests = statusFilter.apply(recommendationRequestStream,
                new RecommendationRequestFilterDto(
                    null,
                    null,
                    null,
                    PENDING
                ));

        List<RecommendationRequest> recommendationRequestList = recommendationRequests.toList();

        assertEquals(2, recommendationRequestList.size());
        assertEquals(PENDING, recommendationRequestList.get(0).getStatus());
    }
}
