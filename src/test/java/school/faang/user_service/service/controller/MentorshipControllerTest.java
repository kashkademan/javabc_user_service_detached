package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.dto.mentorship.GetMenteesResponseDto;
import school.faang.user_service.dto.mentorship.GetMentorsResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MenteeMapperImpl;
import school.faang.user_service.mapper.mentorship.MentorMapperImpl;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @Spy
    MenteeMapperImpl menteeMapper;

    @Spy
    MentorMapperImpl mentorsMapper;

    @Mock
    MentorshipService mentorshipService;

    @InjectMocks
    MentorshipController mentorshipController;

    private User createUser(long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    @Test
    public void testGetMentees() {
        User mentor = createUser(1L, "Miras");
        User mentee = createUser(2L, "Jon");
        mentee.setMentors(List.of(mentor));

        List<GetMenteesResponseDto> mentees = List.of(new GetMenteesResponseDto(2L, "Jon"));

        when(mentorshipService.getMentees(mentor.getId()))
                .thenReturn(mentees);

        List<GetMenteesResponseDto> result = mentorshipController.getMentees(mentor.getId());

        assertEquals(mentees, result);
        verify(mentorshipService, times(1))
                .getMentees(mentor.getId());
    }


    @Test
    public void testGetMentors() {
        User mentor = createUser(1L, "Miras");
        User mentee = createUser(2L, "Jon");
        mentor.setMentees(List.of(mentee));

        List<GetMentorsResponseDto> mentors = List.of(new GetMentorsResponseDto(1L, "Miras"));

        when(mentorshipService.getMentors(mentee.getId())).thenReturn(mentors);

        List<GetMentorsResponseDto> result = mentorshipController.getMentors(mentee.getId());

        assertEquals(mentors, result);
        verify(mentorshipService, times(1)).getMentors(mentee.getId());
    }


    @Test
    public void testDeleteMentee() {
        User mentor = createUser(1L, "Miras");
        User mentee = createUser(2L, "Jon");
        mentor.setMentees(List.of(mentee));
        mentee.setMentors(List.of(mentor));

        mentorshipController.deleteMentee(mentee.getId(), mentor.getId());

        verify(mentorshipService, times(1)).deleteMentee(mentee.getId(), mentor.getId());
    }

    @Test
    public void testDeleteMentor() {
        User mentor = createUser(1L, "Miras");
        User mentee = createUser(2L, "Jon");
        mentor.setMentees(List.of(mentee));
        mentee.setMentors(List.of(mentor));

        mentorshipController.deleteMentor(mentee.getId(), mentor.getId());

        verify(mentorshipService, times(1))
                .deleteMentor(mentee.getId(), mentor.getId());
    }
}
