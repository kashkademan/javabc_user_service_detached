package school.faang.user_service.filter.mentorship_request;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReceiverFilterTest {
    ReceiverFilter filter = new ReceiverFilter();

    @Test
    void testIsApplicableReceiverIsNull() {
        MentorshipRequestFilterDto dto = createDto(null);

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableReceiverIsNotNull() {
        MentorshipRequestFilterDto dto = createDto(1L);

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testApplyFoundOne() {
        Stream<MentorshipRequest> requests = Stream.of(
                createMentorshipRequest(1L, 1L),
                createMentorshipRequest(2L, 2L),
                createMentorshipRequest(3L, 3L)
        );

        MentorshipRequestFilterDto dto = createDto(1L);

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(1, list.size());
    }

    @Test
    void testApplyNoneFound() {
        Stream<MentorshipRequest> requests = Stream.of(
                createMentorshipRequest(1L, 1L),
                createMentorshipRequest(2L, 2L),
                createMentorshipRequest(3L, 3L)
        );

        MentorshipRequestFilterDto dto = createDto(4L);

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(0, list.size());
    }

    @Test
    void testApplyFoundTwo() {
        Stream<MentorshipRequest> requests = Stream.of(
                createMentorshipRequest(1L, 1L),
                createMentorshipRequest(2L, 1L),
                createMentorshipRequest(3L, 2L)
        );

        MentorshipRequestFilterDto dto = createDto(1L);

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(2, list.size());
    }

    @Test
    void testApplyNegativeId() {
        Stream<MentorshipRequest> requests = Stream.of(
                createMentorshipRequest(1L, 1L),
                createMentorshipRequest(2L, 1L),
                createMentorshipRequest(3L, 2L)
        );

        MentorshipRequestFilterDto dto = createDto(-1L);

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(0, list.size());
    }

    private MentorshipRequest createMentorshipRequest(Long requestId, Long userId) {
        return new MentorshipRequest(requestId, null, null,
                createUserById(userId), null, null, null, null);
    }

    private User createUserById(Long id) {
        return User.builder().id(id).build();
    }

    private MentorshipRequestFilterDto createDto(Long receiverId) {
        return new MentorshipRequestFilterDto(null, null, receiverId, null);
    }
}