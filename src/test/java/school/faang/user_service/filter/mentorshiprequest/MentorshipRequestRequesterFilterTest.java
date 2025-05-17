package school.faang.user_service.filter.mentorshiprequest;


import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorshipRequestRequesterFilterTest {
    private final MentorshipRequestFilter filter = new MentorshipRequestRequesterFilter();

    @Test
    public void testIsApplicable_whenRequesterIsNotNull_thenReturnTrue() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().requesterIdPattern(1L).build());
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_whenRequesterIsNull_thenReturnFalse() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().build());
        assertFalse(result);
    }

    @Test
    public void testApply_whenNotAllFiltersPassed_thenNecessaryRequests() {
        Long pattern = 1L;
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().requester(User.builder().id(pattern).build()).build(),
                MentorshipRequest.builder().requester(User.builder().id(10L).build()).build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().requesterIdPattern(pattern).build())
                .toList();
        assertEquals(1, filteredStream.size());
        assertEquals(pattern, filteredStream.get(0).getRequester().getId());
    }

    @Test
    public void testApply_whenFiltersIsPassed_thenReturnAllRequests() {
        Long pattern = 1L;
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().requester(User.builder().id(pattern).build()).build(),
                MentorshipRequest.builder().requester(User.builder().id(pattern).build()).build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().requesterIdPattern(pattern).build())
                .toList();
        assertEquals(2, filteredStream.size());
        assertEquals(pattern, filteredStream.get(0).getRequester().getId());
        assertEquals(pattern, filteredStream.get(1).getRequester().getId());
    }

    @Test
    public void testApply_whenAllFiltersFailed_thenReturnEmptyList() {
        Long pattern = 1L;
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().requester(User.builder().id(9L).build()).build(),
                MentorshipRequest.builder().requester(User.builder().id(10L).build()).build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().requesterIdPattern(pattern).build())
                .toList();
        assertEquals(0, filteredStream.size());
    }
}