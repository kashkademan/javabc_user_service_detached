package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceFacadeTest {
    private static final long MENTOR_ID = 1L;
    private static final long MENTEE_ID = 2L;

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private MentorshipServiceFacade mentorshipServiceFacade;

    @Test
    public void testGetMentees() {
        when(mentorshipService.getMentees(MENTOR_ID)).thenReturn(Collections.emptyList());

        List<MenteeDto> result = mentorshipServiceFacade.getMentees(MENTOR_ID);

        verify(mentorshipService, times(1)).getMentees(MENTOR_ID);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMentors() {
        when(mentorshipService.getMentors(MENTEE_ID)).thenReturn(Collections.emptyList());

        List<MentorDto> result = mentorshipServiceFacade.getMentors(MENTEE_ID);

        verify(mentorshipService, times(1)).getMentors(MENTEE_ID);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}