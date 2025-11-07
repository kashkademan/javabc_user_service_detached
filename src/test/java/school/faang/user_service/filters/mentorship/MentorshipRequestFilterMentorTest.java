package school.faang.user_service.filters.mentorship;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.service.mentorship.DataForTests;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MentorshipRequestFilterMentorTest extends DataForTests {

    private final MentorshipRequestFilter filter = new MentorshipRequestFilterMentor();


    @Test
    void isApplicable_filterDoesNotExist() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto(
                MENTEE_ID_1,
                MENTOR_ID_NULL,
                REQUEST_STATUS_NULL);

        assertFalse(filter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    void isApplicable_filterExists() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto(
                MENTEE_ID_NULL,
                MENTOR_ID_3,
                REQUEST_STATUS_NULL);

        assertTrue(filter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    void apply_AllMentoringRequestsForMentorHaveBeenFound() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto(
                MENTEE_ID_NULL,
                MENTOR_ID_3,
                REQUEST_STATUS_NULL);

        List<MentorshipRequest> resultMentorshipRequest = List.of(
                mentReqP23,
                mentReqP43,
                mentReqR13
        );

        List<MentorshipRequest> resultFiltredMentorshipRequest = filter
                .apply(mentorshipRequestAll.stream(), mentorshipRequestFilterDto)
                .toList();

        assertEquals(resultFiltredMentorshipRequest.size(), resultMentorshipRequest.size());
        assertEquals(new HashSet<>(resultFiltredMentorshipRequest),
                new HashSet<>(resultMentorshipRequest));
    }
}