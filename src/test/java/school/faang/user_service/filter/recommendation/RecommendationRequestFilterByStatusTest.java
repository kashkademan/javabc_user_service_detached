package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationRequestFilterByStatusTest {
    private final RecommendationRequestFilterByStatus filter = new RecommendationRequestFilterByStatus();

    @Test
    public void isApplicableTrue() {
        boolean result = filter.isApplicable(RequestFilterDto.builder()
                .status(RequestStatus.PENDING)
                .build());

        assertTrue(result);
    }

    @Test
    public void isApplicableFalse() {
        boolean result = filter.isApplicable(
                new RequestFilterDto(null, null, null, null)
        );
        assertFalse(result);
    }

    @Test
    public void testGetEmptyResult() {
        Stream<RecommendationRequest> requestStream = getMockStreamRequest();
        List<RecommendationRequest> resultList =
                filter.apply(requestStream, getMockFilter(RequestStatus.ACCEPTED)).toList();

        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    public void testGetEmptyResultFromEmptyStream() {
        Stream<RecommendationRequest> requestStream = Stream.empty();
        List<RecommendationRequest> resultList =
                filter.apply(requestStream, getMockFilter(RequestStatus.ACCEPTED)).toList();

        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    public void testGetExistsResult() {
        Stream<RecommendationRequest> requestStream = getMockStreamRequest();
        List<RecommendationRequest> resultList =
                filter.apply(requestStream, getMockFilter(RequestStatus.PENDING)).toList();

        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals(RequestStatus.PENDING, resultList.get(0).getStatus());
        assertEquals(RequestStatus.PENDING, resultList.get(1).getStatus());
    }

    private Stream<RecommendationRequest> getMockStreamRequest() {
        return Stream.of(
                getMockRequest(RequestStatus.REJECTED),
                getMockRequest(RequestStatus.REJECTED),
                getMockRequest(RequestStatus.PENDING),
                getMockRequest(RequestStatus.PENDING)
        );
    }

    private RecommendationRequest getMockRequest(RequestStatus status) {
        return RecommendationRequest.builder()
                .status(status)
                .build();
    }

    private RequestFilterDto getMockFilter(RequestStatus status) {
        return RequestFilterDto.builder()
                .status(status)
                .build();
    }

}