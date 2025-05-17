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
    private static final String DESCRIPTION_PATTERN = "request";

    private final MentorshipRequestFilter filter = new MentorshipRequestDescriptionFilter();

    @Test
    public void testIsApplicable_whenDescriptionIsNotNull_thenReturnTrue() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().descriptionPattern("string").build());
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_whenDescriptionIsNull_thenReturnFalse() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().build());
        assertFalse(result);
    }

    @Test
    public void testIsApplicable_whenDescriptionIsBlank_thenReturnFalse() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().descriptionPattern("     ").build());
        assertFalse(result);
    }

    @Test
    public void testApply_whenNotAllFiltersPassed_thenNecessaryRequests() {
        Stream<MentorshipRequest> requests = initializeRequestsStream(DESCRIPTION_PATTERN, "empty");
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().descriptionPattern(DESCRIPTION_PATTERN).build())
                .toList();
        assertEquals(1, filteredStream.size());
        assertEquals(DESCRIPTION_PATTERN, filteredStream.get(0).getDescription());
    }

    @Test
    public void testApply_whenFiltersIsPassed_thenReturnAllRequests() {
        Stream<MentorshipRequest> requests = initializeRequestsStream(DESCRIPTION_PATTERN, DESCRIPTION_PATTERN);
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().descriptionPattern(DESCRIPTION_PATTERN).build())
                .toList();
        assertEquals(2, filteredStream.size());
        assertEquals(DESCRIPTION_PATTERN, filteredStream.get(0).getDescription());
        assertEquals(DESCRIPTION_PATTERN, filteredStream.get(1).getDescription());
    }

    @Test
    public void testApply_whenAllFiltersFailed_thenReturnEmptyList() {
        Stream<MentorshipRequest> requests = initializeRequestsStream("empty", "empty");
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().descriptionPattern(DESCRIPTION_PATTERN).build())
                .toList();
        assertEquals(0, filteredStream.size());
    }

    @Test
    public void testApply_whenRequestsDescriptionsFailCase_thenReturnEmptyList() {
        Stream<MentorshipRequest> requests = initializeRequestsStream("rEqUeSt", "ReQuEsT");
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().descriptionPattern(DESCRIPTION_PATTERN).build())
                .toList();
        assertEquals(0, filteredStream.size());
    }

    private Stream<MentorshipRequest> initializeRequestsStream(String... descriptions) {
        return  Stream.of(descriptions)
                .map(description -> MentorshipRequest.builder().description(description).build());
    }
}