package school.faang.user_service.filter.mentorshiprequest;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MentorshipRequestStatusFilterTest {
    private final MentorshipRequestFilter filter = new MentorshipRequestStatusFilter();

    @Test
    public void testIsApplicable_whenStatusIsNotNull_thenReturnTrue() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().statusPattern(RequestStatus.ACCEPTED).build());
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_whenStatusIsNull_thenReturnFalse() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().build());
        assertFalse(result);
    }

    @Test
    public void testApply_whenFilterIsFailed_thenReturnEmptyList() {
        RequestStatus pattern = RequestStatus.ACCEPTED;
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().status(pattern).build(),
                MentorshipRequest.builder().status(RequestStatus.REJECTED).build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().statusPattern(pattern).build())
                .toList();
        assertEquals(1, filteredStream.size());
        assertEquals(pattern, filteredStream.get(0).getStatus());
    }

    @Test
    public void testApply_whenFiltersIsPassed_thenReturnAllRequests() {
        RequestStatus pattern = RequestStatus.ACCEPTED;
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().status(pattern).build(),
                MentorshipRequest.builder().status(pattern).build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().statusPattern(pattern).build())
                .toList();
        assertEquals(2, filteredStream.size());
        assertEquals(pattern, filteredStream.get(0).getStatus());
        assertEquals(pattern, filteredStream.get(1).getStatus());
    }

    @Test
    public void testApply_whenAllFiltersFailed_thenReturnEmptyList() {
        RequestStatus pattern = RequestStatus.ACCEPTED;
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().status(RequestStatus.REJECTED).build(),
                MentorshipRequest.builder().status(RequestStatus.REJECTED).build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().statusPattern(pattern).build())
                .toList();
        assertEquals(0, filteredStream.size());
    }
}