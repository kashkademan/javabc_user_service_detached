package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class RecommendationRequestFilterByMessagePatternTest {
    private final RecommendationRequestFilterByMessagePattern filter =
            new RecommendationRequestFilterByMessagePattern();

    @Test
    public void isApplicableTrue() {
        boolean result = filter.isApplicable(getMockFilter("message"));
        assertTrue(result);
    }

    @Test
    public void testNonApplicableMessageIsNull() {
        boolean result = filter.isApplicable(
                new RequestFilterDto(null, null, null, null)
        );
        assertFalse(result);
    }

    @Test
    public void testNonApplicableMessageIsEmpty() {
        boolean result = filter.isApplicable(getMockFilter(""));
        assertFalse(result);
    }

    @Test
    public void testNonApplicableMessageIsBlank() {
        boolean result = filter.isApplicable(getMockFilter("  "));
        assertFalse(result);
    }

    @Test
    public void testMessageGetEmptyResult() {
        Stream<RecommendationRequest> request = getMockStreamRequest();
        List<RecommendationRequest> resultList = filter.apply(request, getMockFilter("text")).toList();
        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    public void testMessageGetOneResult() {
        Stream<RecommendationRequest> request = getMockStreamRequest();
        List<RecommendationRequest> resultList = filter.apply(request, getMockFilter("ThirD")).toList();
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals("third message", resultList.get(0).getMessage().toLowerCase());
    }

    @Test
    public void testMessageCheckCaseResult() {
        Stream<RecommendationRequest> request = getMockStreamRequest();
        List<RecommendationRequest> resultList = filter.apply(request, getMockFilter("THIRD")).toList();
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals("third message", resultList.get(0).getMessage().toLowerCase());
    }

    @Test
    public void testMessageGetAllResult() {
        Stream<RecommendationRequest> request = getMockStreamRequest();
        List<RecommendationRequest> resultList = filter.apply(request, getMockFilter("Message")).toList();
        assertNotNull(resultList);
        assertEquals(4, resultList.size());
    }

    private Stream<RecommendationRequest> getMockStreamRequest() {
        return Stream.of(
                getMockRequest("fIRSt meSSage"),
                getMockRequest("seCOnd meSSage"),
                getMockRequest("tHIrd meSSage"),
                getMockRequest("foURth meSSage")
        );
    }

    private RequestFilterDto getMockFilter(String pattern) {
        return RequestFilterDto.builder()
                .messagePattern(pattern)
                .build();
    }

    private RecommendationRequest getMockRequest(String requestMessage) {
        return RecommendationRequest.builder()
                .message(requestMessage)
                .build();
    }
}