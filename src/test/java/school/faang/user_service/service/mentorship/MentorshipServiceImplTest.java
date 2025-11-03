package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipServiceImpl;
import school.faang.user_service.repository.mentorship.service.validation.MentorshipServiceValidation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceImplTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private MentorshipServiceValidation validation;

    @Test
    void addMentorshipTest() {
        long mentorId = 1L;
        long menteeId = 2L;

        User mentor = new User();
        mentor.setId(mentorId);
        mentor.setMentees(new ArrayList<>());

        User mentee = new User();
        mentee.setId(menteeId);
        mentee.setMentors(new ArrayList<>());

        long currentUserId = 1L;

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(mentorshipRepository.getByIdOrThrow(mentorId)).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);

        mentorshipService.addMentorship(mentorId, menteeId);

        assertTrue(mentor.getMentees().contains(mentee));
        assertTrue(mentee.getMentors().contains(mentor));
        assertEquals(1, mentor.getMentees().size());
        assertEquals(1, mentee.getMentors().size());
        verify(mentorshipRepository).save(mentee);
        verify(mentorshipRepository).save(mentor);
    }

    @Test
    void getMenteesTest() {
        // Arrange
        long userId = 1L;
        User mentor = new User();
        mentor.setId(userId);

        User mentee1 = new User();
        mentee1.setId(2L);
        mentee1.setUsername("mentee1");

        User mentee2 = new User();
        mentee2.setId(3L);
        mentee2.setUsername("mentee2");

        List<User> mentees = new ArrayList<>();

        mentees.add(mentee1);
        mentees.add(mentee2);
        mentor.setMentees(mentees);

        UserDto menteeDto1 = new UserDto(
                2L,
                "mentee1",
                "chtoto@gdeto.ru",
                "+87776665432",
                "1"
        );

        UserDto menteeDto2 = new UserDto(
                3L,
                "mentee2",
                "gdeto@kakto.ru",
                "+12223334455",
                "2"
        );

        when(mentorshipRepository.getByIdOrThrow(userId)).thenReturn(mentor);
        when(userMapper.toUserDto(mentee1)).thenReturn(menteeDto1);
        when(userMapper.toUserDto(mentee2)).thenReturn(menteeDto2);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(menteeDto1));
        assertTrue(result.contains(menteeDto2));
        verify(mentorshipRepository).getByIdOrThrow(userId);
        verify(userMapper).toUserDto(mentee1);
        verify(userMapper).toUserDto(mentee2);
    }

    @Test
    void getMentorsTest() {
        long userId = 1L;
        User mentee = new User();
        mentee.setId(userId);

        User mentor1 = new User();
        mentor1.setId(2L);
        mentor1.setUsername("mentor1");

        User mentor2 = new User();
        mentor2.setId(3L);
        mentor2.setUsername("mentor2");

        List<User> mentors = new ArrayList<>();

        mentors.add(mentor1);
        mentors.add(mentor2);
        mentee.setMentors(mentors);

        UserDto mentorDto1 = new UserDto(
                2L,
                "mentor1",
                "chtoto@gdeto.ru",
                "+87776665432",
                "1"
        );
        UserDto mentorDto2 = new UserDto(
                3L,
                "mentor2",
                "gdeto@kakto.ru",
                "+12223334455",
                "2"
        );

        when(mentorshipRepository.getByIdOrThrow(userId)).thenReturn(mentee);
        when(userMapper.toUserDto(mentor1)).thenReturn(mentorDto1);
        when(userMapper.toUserDto(mentor2)).thenReturn(mentorDto2);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(mentorDto1));
        assertTrue(result.contains(mentorDto2));
        verify(mentorshipRepository).getByIdOrThrow(userId);
        verify(userMapper).toUserDto(mentor1);
        verify(userMapper).toUserDto(mentor2);
    }

    @Test
    void deleteMentorshipTest() {
        long mentorId = 1L;
        long menteeId = 2L;

        User mentor = new User();
        mentor.setId(mentorId);
        mentor.setMentees(new ArrayList<>());

        User mentee = new User();
        mentee.setId(menteeId);
        mentee.setMentors(new ArrayList<>());

        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);

        long currentUserId = 1L;

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(mentorshipRepository.getByIdOrThrow(mentorId)).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);
        when(validation.validationCurrentUser(currentUserId, mentorId, menteeId)).thenReturn(true);
        when(validation.validationIds(mentorId, menteeId)).thenReturn(true);

        mentorshipService.deleteMentorship(menteeId, mentorId);

        assertTrue(mentor.getMentees().isEmpty());
        assertTrue(mentee.getMentors().isEmpty());
        verify(mentorshipRepository).save(mentee);
        verify(mentorshipRepository).save(mentor);
    }
}