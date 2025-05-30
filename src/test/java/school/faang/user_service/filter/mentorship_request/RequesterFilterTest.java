package school.faang.user_service.filter.mentorship_request;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RequesterFilterTest {
    RequesterFilter filter = new RequesterFilter();

    @Test
    void testIsApplicableRequesterIsNull() {
        MentorshipRequestFilterDto dto = createDto(null);

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableRequesterIsNotNull() {
        MentorshipRequestFilterDto dto = createDto(1L);

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testApplyFoundOne() {
        Long requesterId1 = 1L;
        Long requesterId2 = 2L;
        Long requesterId3 = 3L;

        Stream<MentorshipRequest> requests = Stream.of(
                createMentorshipRequest(requesterId1, requesterId1),
                createMentorshipRequest(requesterId2, requesterId2),
                createMentorshipRequest(requesterId3, requesterId3)
        );

        MentorshipRequestFilterDto dto = createDto(requesterId1);

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(1, list.size());
    }

    @Test
    void testApplyNoneFound() {
        Long requesterId1 = 1L;
        Long requesterId2 = 2L;
        Long requesterId3 = 3L;
        Long requesterId4 = 4L;

        Stream<MentorshipRequest> requests = Stream.of(
                createMentorshipRequest(requesterId1, requesterId1),
                createMentorshipRequest(requesterId2, requesterId2),
                createMentorshipRequest(requesterId3, requesterId3)
        );

        MentorshipRequestFilterDto dto = createDto(requesterId4);

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(0, list.size());
    }

    @Test
    void testApplyFoundTwo() {
        Long requesterId1 = 1L;
        Long requesterId2 = 2L;
        Long requesterId3 = 3L;

        Stream<MentorshipRequest> requests = Stream.of(
                createMentorshipRequest(requesterId1, requesterId1),
                createMentorshipRequest(requesterId2, requesterId1),
                createMentorshipRequest(requesterId3, requesterId3)
        );

        MentorshipRequestFilterDto dto = createDto(requesterId1);

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(2, list.size());
    }

    private MentorshipRequest createMentorshipRequest(Long requestId, Long userId) {
        return new MentorshipRequest(requestId, null, createUserById(userId), null, null, null, null, null);
    }

    private User createUserById(Long id) {
        return User.builder().id(id).build();
    }

    private MentorshipRequestFilterDto createDto(Long requesterId) {
        return new MentorshipRequestFilterDto(null, requesterId, null, null);
    }
}