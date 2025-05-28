package school.faang.user_service.service.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestFilterTest {

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private RecommendationRequest request;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        Recommendation recommendation = new Recommendation();
        recommendation.setId(3L);

        request = RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .recommendation(recommendation)
                .message("Java developer needed")
                .createdAt(now.minusDays(1))
                .build();
    }

    @Test
    void filterByRequesterId_ShouldReturnTrue_WhenRequesterIdMatches() {
        assertTrue(recommendationRequestService.filterByRequesterId(request, 1L));
    }

    @Test
    void filterByRequesterId_ShouldReturnTrue_WhenRequesterIdIsNull() {
        assertTrue(recommendationRequestService.filterByRequesterId(request, null));
    }

    @Test
    void filterByRequesterId_ShouldReturnFalse_WhenRequesterIdDoesNotMatch() {
        assertFalse(recommendationRequestService.filterByRequesterId(request, 999L));
    }

    @Test
    void filterByReceiverId_ShouldReturnTrue_WhenReceiverIdMatches() {
        assertTrue(recommendationRequestService.filterByReceiverId(request, 2L));
    }

    @Test
    void filterByReceiverId_ShouldReturnTrue_WhenReceiverIdIsNull() {
        assertTrue(recommendationRequestService.filterByReceiverId(request, null));
    }

    @Test
    void filterByReceiverId_ShouldReturnFalse_WhenReceiverIdDoesNotMatch() {
        assertFalse(recommendationRequestService.filterByReceiverId(request, 999L));
    }

    @Test
    void filterByRecommendationId_ShouldReturnTrue_WhenRecommendationIdMatches() {
        assertTrue(recommendationRequestService.filterByRecommendationId(request, 3L));
    }

    @Test
    void filterByRecommendationId_ShouldReturnTrue_WhenRecommendationIdIsNull() {
        assertTrue(recommendationRequestService.filterByRecommendationId(request, null));
    }

    @Test
    void filterByRecommendationId_ShouldReturnFalse_WhenRecommendationIdDoesNotMatch() {
        assertFalse(recommendationRequestService.filterByRecommendationId(request, 999L));
    }

    @Test
    void filterByRecommendationId_ShouldReturnFalse_WhenRecommendationIsNull() {
        request.setRecommendation(null);
        assertFalse(recommendationRequestService.filterByRecommendationId(request, 3L));
    }

    @Test
    void filterByMessagePattern_ShouldReturnTrue_WhenMessageContainsPattern() {
        assertTrue(recommendationRequestService.filterByMessagePattern(request, "java"));
    }

    @Test
    void filterByMessagePattern_ShouldReturnTrue_WhenPatternIsNull() {
        assertTrue(recommendationRequestService.filterByMessagePattern(request, null));
    }

    @Test
    void filterByMessagePattern_ShouldReturnFalse_WhenMessageDoesNotContainPattern() {
        assertFalse(recommendationRequestService.filterByMessagePattern(request, "python"));
    }

    @Test
    void filterByMessagePattern_ShouldReturnFalse_WhenMessageIsNull() {
        request.setMessage(null);
        assertFalse(recommendationRequestService.filterByMessagePattern(request, "java"));
    }

    @Test
    void filterByCreatedAfter_ShouldReturnTrue_WhenCreatedAfterGivenDate() {
        assertTrue(recommendationRequestService.filterByCreatedAfter(request, now.minusDays(2)));
    }

    @Test
    void filterByCreatedAfter_ShouldReturnTrue_WhenDateIsNull() {
        assertTrue(recommendationRequestService.filterByCreatedAfter(request, null));
    }

    @Test
    void filterByCreatedAfter_ShouldReturnFalse_WhenCreatedBeforeGivenDate() {
        assertFalse(recommendationRequestService.filterByCreatedAfter(request, now));
    }

    @Test
    void filterByCreatedBefore_ShouldReturnTrue_WhenCreatedBeforeGivenDate() {
        assertTrue(recommendationRequestService.filterByCreatedBefore(request, now));
    }

    @Test
    void filterByCreatedBefore_ShouldReturnTrue_WhenDateIsNull() {
        assertTrue(recommendationRequestService.filterByCreatedBefore(request, null));
    }

    @Test
    void filterByCreatedBefore_ShouldReturnFalse_WhenCreatedAfterGivenDate() {
        assertFalse(recommendationRequestService.filterByCreatedBefore(request, now.minusDays(2)));
    }
}