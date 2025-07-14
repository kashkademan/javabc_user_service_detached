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

class RecommendationRequestReceiverIdFilterTest {
    private final RecommendationRequestReceiverIdFilter receiverIdFilter = new RecommendationRequestReceiverIdFilter();

    @Test
    @DisplayName("ReceiverId Filter is applicable")
    void isApplicableTrue() {
        boolean applicable = receiverIdFilter.isApplicable(new RecommendationRequestFilterDto(
                null,
                1L,
                null,
                null));

        assertTrue(applicable);
    }

    @Test
    @DisplayName("ReceiverId Filter is not applicable")
    void testIsApplicableFalse() {
        boolean applicable = receiverIdFilter.isApplicable(new RecommendationRequestFilterDto(
                null,
                null,
                null,
                null));

        assertFalse(applicable);
    }

    @Test
    @DisplayName("ReceiverId Filter applies correctly")
    void testApply() {
        Stream<RecommendationRequest> recommendationRequestStream = Stream.of(
                RecommendationRequest.builder()
                        .receiver(User.builder()
                                .id(1L)
                                .build())
                        .build(),
                RecommendationRequest.builder()
                        .receiver(User.builder()
                                .id(2L)
                                .build())
                        .build()
        );

        Stream<RecommendationRequest> recommendationRequests = receiverIdFilter.apply(recommendationRequestStream,
                new RecommendationRequestFilterDto(
                        null,
                        2L,
                        null,
                        null
                ));

        List<RecommendationRequest> recommendationRequestList = recommendationRequests.toList();

        assertEquals(1, recommendationRequestList.size());
        assertEquals(2L, recommendationRequestList.get(0).getReceiver().getId());
    }
}
