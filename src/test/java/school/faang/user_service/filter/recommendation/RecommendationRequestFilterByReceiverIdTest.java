package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationRequestFilterByReceiverIdTest {
    private final RecommendationRequestFilterByReceiverId filter = new RecommendationRequestFilterByReceiverId();

    @Test
    public void isApplicableTrue() {
        boolean result = filter.isApplicable(getMockDto(1L));
        assertTrue(result);
    }

    @Test
    public void testNonApplicableIdIsNull() {
        boolean result = filter.isApplicable(
                new RequestFilterDto(null, null, null, null)
        );
        assertFalse(result);
    }

    @Test
    public void testNonApplicableIdIsNegative() {
        boolean result = filter.isApplicable(getMockDto(-1L));
        assertFalse(result);
    }

    @Test
    public void testSuccessfulApplyFilter() {
        Stream<RecommendationRequest> request = getMockStreamRequest();
        List<RecommendationRequest> resultList = filter.apply(request, getMockFilter(2L)).toList();
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(2L, resultList.get(0).getReceiver().getId());
    }

    @Test
    public void testNoDataFoundFilter() {
        Stream<RecommendationRequest> request = getMockStreamRequest();
        List<RecommendationRequest> resultList = filter.apply(request, getMockFilter(5L)).toList();
        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    private RequestFilterDto getMockFilter(Long receiverId) {
        return RequestFilterDto.builder()
                .receiverId(receiverId)
                .build();
    }

    private Stream<RecommendationRequest> getMockStreamRequest() {
        return Stream.of(
                getMockRequest(1L),
                getMockRequest(2L),
                getMockRequest(3L),
                getMockRequest(4L)
        );
    }

    private RequestFilterDto getMockDto(Long id) {
        return RequestFilterDto.builder()
                .receiverId(id)
                .build();

    }

    private RecommendationRequest getMockRequest(Long id) {
        return RecommendationRequest.builder()
                .receiver(getMockUser(id))
                .build();
    }

    private User getMockUser(Long id) {
        return User.builder()
                .id(id)
                .username("user-%d".formatted(id))
                .build();
    }
}