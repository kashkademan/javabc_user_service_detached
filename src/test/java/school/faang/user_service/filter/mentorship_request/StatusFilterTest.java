package school.faang.user_service.filter.mentorship_request;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StatusFilterTest {
    StatusFilter filter = new StatusFilter();

    @Test
    void testIsApplicableStatusIsNull() {
        MentorshipRequestFilterDto dto = createDto(null);

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableRequesterIsNotNull() {
        MentorshipRequestFilterDto dto = createDto(RequestStatus.ACCEPTED);

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testApplyFoundOne() {
        Stream<MentorshipRequest> requests = Stream.of(
                createMentorshipRequest(1L, RequestStatus.ACCEPTED),
                createMentorshipRequest(2L, RequestStatus.REJECTED),
                createMentorshipRequest(3L, RequestStatus.PENDING)
        );

        MentorshipRequestFilterDto dto = createDto(RequestStatus.ACCEPTED);

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(1, list.size());
    }

    @Test
    void testApplyNoneFound() {
        Stream<MentorshipRequest> requests = Stream.of(
                createMentorshipRequest(1L, RequestStatus.ACCEPTED),
                createMentorshipRequest(2L, RequestStatus.ACCEPTED),
                createMentorshipRequest(3L, RequestStatus.ACCEPTED)
        );

        MentorshipRequestFilterDto dto = createDto(RequestStatus.PENDING);

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(0, list.size());
    }

    @Test
    void testApplyFoundTwo() {
        Stream<MentorshipRequest> requests = Stream.of(
                createMentorshipRequest(1L, RequestStatus.ACCEPTED),
                createMentorshipRequest(2L, RequestStatus.ACCEPTED),
                createMentorshipRequest(3L, RequestStatus.ACCEPTED)
        );

        MentorshipRequestFilterDto dto = createDto(RequestStatus.ACCEPTED);

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(3, list.size());
    }

    private MentorshipRequest createMentorshipRequest(Long requestId, RequestStatus status) {
        return new MentorshipRequest(requestId, null, null, null, status, null, null, null);
    }

    private MentorshipRequestFilterDto createDto(RequestStatus status) {
        return new MentorshipRequestFilterDto(null, null, null, status);
    }
}