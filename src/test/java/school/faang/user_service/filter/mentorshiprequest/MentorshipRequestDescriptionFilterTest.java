package school.faang.user_service.filter.mentorshiprequest;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MentorshipRequestDescriptionFilterTest {
    private final MentorshipRequestFilter filter = new MentorshipRequestDescriptionFilter();

    @Test
    public void testRequestIsApplicable() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().descriptionPattern("string").build());
        assertTrue(result);
    }

    @Test
    public void testRequestIsNotApplicable() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().build());
        assertFalse(result);
    }

    @Test
    public void testRequestIsNotApplicableWhenPatternIsBlank() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().descriptionPattern("     ").build());
        assertFalse(result);
    }

    @Test
    public void testApplyRequestFilter() {
        String pattern = "request";
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().description(pattern).build(),
                MentorshipRequest.builder().description("empty").build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().descriptionPattern(pattern).build())
                .toList();
        assertEquals(1, filteredStream.size());
        assertEquals(pattern, filteredStream.get(0).getDescription());
    }

    @Test
    public void testApplySeveralApplicableRequests() {
        String pattern = "request";
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().description(pattern).build(),
                MentorshipRequest.builder().description(pattern).build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().descriptionPattern(pattern).build())
                .toList();
        assertEquals(2, filteredStream.size());
        assertEquals(pattern, filteredStream.get(0).getDescription());
        assertEquals(pattern, filteredStream.get(1).getDescription());
    }

    @Test
    public void testApplyNoneApplicableRequests() {
        String pattern = "request";
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().description("empty").build(),
                MentorshipRequest.builder().description("empty").build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().descriptionPattern(pattern).build())
                .toList();
        assertEquals(0, filteredStream.size());
    }

    @Test
    public void testApplyRequestsNotIgnoreCase() {
        String pattern = "request";
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().description("rEqUeSt").build(),
                MentorshipRequest.builder().description("ReQuEsT").build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().descriptionPattern(pattern).build())
                .toList();
        assertEquals(0, filteredStream.size());
    }
}