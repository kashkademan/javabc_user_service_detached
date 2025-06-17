package school.faang.user_service.filter.mentorship_request;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DescriptionFilterTest {
    DescriptionFilter filter = new DescriptionFilter();

    @Test
    void testIsApplicableDescriptionNull() {
        MentorshipRequestFilterDto dto = createDto(null);

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableDescriptionIsBlank() {
        MentorshipRequestFilterDto dto = createDto("   ");

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void testIsApplicableDescriptionTrue() {
        MentorshipRequestFilterDto dto = createDto("test");

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void testApplyAllFit() {
        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, "test 1 qwe", null, null, null, null, null, null),
                new MentorshipRequest(2L, "tEst 2 qer", null, null, null, null, null, null),
                new MentorshipRequest(3L, "tesT 3 qaz", null, null, null, null, null, null)
        );

        MentorshipRequestFilterDto dto = createDto("test");

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(3, list.size());
        assertEquals("test 3 qaz", list.get(2).getDescription().toLowerCase());
    }

    @Test
    void testApplyOneComingOneWord() {
        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, "test one", null, null, null, null, null, null),
                new MentorshipRequest(2L, "tEst two", null, null, null, null, null, null),
                new MentorshipRequest(3L, "tesT three", null, null, null, null, null, null)
        );

        MentorshipRequestFilterDto dto = createDto("two");

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(1, list.size());
    }

    @Test
    void testApplyThreeComingTwoWort() {
        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, "test one two", null, null, null, null, null, null),
                new MentorshipRequest(2L, "tEst two three", null, null, null, null, null, null),
                new MentorshipRequest(3L, "tesT three for", null, null, null, null, null, null)
        );

        MentorshipRequestFilterDto dto = createDto("two three");

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(3, list.size());
    }

    @Test
    void testApplyNoSuitable() {
        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, "test one two", null, null, null, null, null, null),
                new MentorshipRequest(2L, "tEst two three", null, null, null, null, null, null),
                new MentorshipRequest(3L, "tesT three for", null, null, null, null, null, null)
        );

        MentorshipRequestFilterDto dto = createDto("something written here");

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(0, list.size());
    }

    @Test
    void testApplySpecialCharacters() {
        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, "test&@ one two", null, null, null, null, null, null),
                new MentorshipRequest(2L, "tEst@ two three", null, null, null, null, null, null),
                new MentorshipRequest(3L, "tesT& three for", null, null, null, null, null, null)
        );

        MentorshipRequestFilterDto dto = createDto("test&@");

        List<MentorshipRequest> list = filter.apply(requests, dto).toList();

        assertEquals(1, list.size());
    }

    private MentorshipRequestFilterDto createDto(String str) {
        return new MentorshipRequestFilterDto(str, null, null, null);
    }
}