package school.faang.user_service.filters.mentorship;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.service.mentorship.DataForTests;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestFilterStatusTest extends DataForTests {

    private final MentorshipRequestFilter filter = new MentorshipRequestFilterStatus();


    @Test
    void isApplicable_filterDoesNotExist() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto(
                MENTEE_ID_NULL,
                MENTOR_ID_NULL,
                REQUEST_STATUS_NULL);

        assertFalse(filter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    void isApplicable_filterExists() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto(
                MENTEE_ID_NULL,
                MENTOR_ID_NULL,
                RequestStatus.ACCEPTED);

        assertTrue(filter.isApplicable(mentorshipRequestFilterDto));
    }

    @Test
    void apply_AllMentoringRequestsForMenteeHaveBeenFound() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto(
                MENTEE_ID_NULL,
                MENTOR_ID_NULL,
                RequestStatus.ACCEPTED);

        List<MentorshipRequest> resultMentorshipRequest = List.of(
                mentReqA12,
                mentReqA67,
                mentReqA87,
                mentReqA97
        );

        List<MentorshipRequest> resultFiltredMentorshipRequest = filter
                .apply(mentorshipRequestAll.stream(), mentorshipRequestFilterDto)
                .toList();

        assertEquals(resultFiltredMentorshipRequest.size(), resultMentorshipRequest.size());
        assertEquals(new HashSet<>(resultFiltredMentorshipRequest),
                new HashSet<>(resultMentorshipRequest));
    }
}