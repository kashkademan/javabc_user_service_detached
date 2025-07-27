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
    @DisplayName("Message Contains Filter is null")
    void testIsApplicableIsNull() {
        boolean applicable = messageContainsFilter.isApplicable(new RecommendationRequestFilterDto(
                null,
                null,
                null,
                null));

        assertFalse(applicable);
    }

    @Test
    @DisplayName("Message Contains Filter is empty")
    void testIsApplicableIsEmpty() {
        boolean applicable = messageContainsFilter.isApplicable(new RecommendationRequestFilterDto(
                null,
                null,
                "",
                null));

        assertFalse(applicable);
    }

    @Test
    @DisplayName("Message Contains Filter is blank")
    void testIsApplicableIsBlank() {
        boolean applicable = messageContainsFilter.isApplicable(new RecommendationRequestFilterDto(
                null,
                null,
                "\n  \t",
                null));

        assertFalse(applicable);
    }

    @Test
    @DisplayName("Message Contains Filter applies correctly & has Recommendation Requests")
    void testApplyWithPresence() {
        Stream<RecommendationRequest> recommendationRequestStream = Stream.of(
                RecommendationRequest.builder().message("test message").build(),
                RecommendationRequest.builder().message("other message").build(),
                RecommendationRequest.builder().message("").build(),
                RecommendationRequest.builder().message("null").build(),
                RecommendationRequest.builder().message("  ").build(),
                RecommendationRequest.builder().message("TEST").build());

        Stream<RecommendationRequest> recommendationRequests = messageContainsFilter.apply(recommendationRequestStream,
                new RecommendationRequestFilterDto(
                        null,
                        null,
                        "TeSt",
                        null
                ));

        List<RecommendationRequest> recommendationRequestList = recommendationRequests.toList();

        assertEquals(2, recommendationRequestList.size());
        assertTrue(recommendationRequestList.stream().allMatch(recommendationRequest ->
                recommendationRequest.getMessage().toLowerCase().contains("TeSt".toLowerCase())));
    }

    @Test
    @DisplayName("Message Contains Filter applies correctly & has no Recommendation Requests")
    void testApplyWithoutPresence() {
        Stream<RecommendationRequest> recommendationRequestStream = Stream.of(
                RecommendationRequest.builder().message("test message").build(),
                RecommendationRequest.builder().message("other message").build(),
                RecommendationRequest.builder().message("").build(),
                RecommendationRequest.builder().message("null").build(),
                RecommendationRequest.builder().message("  ").build(),
                RecommendationRequest.builder().message("TEST").build());

        Stream<RecommendationRequest> recommendationRequests = messageContainsFilter.apply(recommendationRequestStream,
                new RecommendationRequestFilterDto(
                        null,
                        null,
                        "java",
                        null
                ));

        List<RecommendationRequest> recommendationRequestList = recommendationRequests.toList();

        assertTrue(recommendationRequestList.isEmpty());
    }
}
