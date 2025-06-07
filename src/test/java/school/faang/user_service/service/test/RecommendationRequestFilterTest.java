package school.faang.user_service.service.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.MessagePatternFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestFilterTest {

    @InjectMocks
    private MessagePatternFilter messagePatternFilter;

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
    void isApplicableShouldReturnTrueWhenMessagePatternIsNotEmpty() {
        RequestFilterDto filters = new RequestFilterDto(null, null, null, "Java", null, null);
        assertTrue(messagePatternFilter.isApplicable(filters));
    }

    @Test
    void isApplicableShouldReturnFalseWhenMessagePatternIsNull() {
        RequestFilterDto filters = new RequestFilterDto(null, null, null, null, null, null);
        assertFalse(messagePatternFilter.isApplicable(filters));
    }

    @Test
    void isApplicableShouldReturnFalseWhenMessagePatternIsBlank() {
        RequestFilterDto filters = new RequestFilterDto(null, null, null, " ", null, null);
        assertFalse(messagePatternFilter.isApplicable(filters));
    }

    @Test
    void applyShouldFilterRequestsContainingMessagePattern() {
        RequestFilterDto filters = new RequestFilterDto(null, null, null, "Java", null, null);

        RecommendationRequest matchingRequest = request;
        RecommendationRequest nonMatchingRequest = RecommendationRequest.builder()
                .id(2L)
                .message("Python developer needed")
                .build();

        Stream<RecommendationRequest> result = messagePatternFilter.apply(
                Stream.of(matchingRequest, nonMatchingRequest),
                filters
        );

        List<RecommendationRequest> filteredRequests = result.collect(Collectors.toList());
        assertEquals(1, filteredRequests.size());
        assertEquals(matchingRequest, filteredRequests.get(0));
    }

    @Test
    void applyShouldReturnEmptyStreamWhenNoRequestsMatchPattern() {
        RequestFilterDto filters = new RequestFilterDto(null, null, null, "React", null, null);

        Stream<RecommendationRequest> result = messagePatternFilter.apply(
                Stream.of(request),
                filters
        );

        assertEquals(0, result.count());
    }

    @Test
    void applyShouldBeCaseSensitive() {
        RequestFilterDto filters = new RequestFilterDto(null, null, null, "java", null, null);

        Stream<RecommendationRequest> result = messagePatternFilter.apply(
                Stream.of(request),
                filters
        );

        assertEquals(0, result.count());
    }

    @Test
    void applyShouldIgnoreOtherFilterFields() {
        RequestFilterDto filters = new RequestFilterDto(999L, 888L, 777L, "Java", now.minusYears(1), now.plusYears(1));

        Stream<RecommendationRequest> result = messagePatternFilter.apply(
                Stream.of(request),
                filters
        );

        assertEquals(1, result.count());
    }
}