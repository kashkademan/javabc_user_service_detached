package school.faang.user_service.service.mentorship;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validation.mentorship.MentorshipValidationImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private MentorshipValidationImpl mentorshipValidation;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    @Test
    public void testMentorshipAdded() {
        long mentorId = 100L;
        long menteeId = 200L;
        boolean validationResult;

        validationResult = mentorshipValidation.canAddMentorship(mentorId,
                menteeId,
                (mentor, mentee) -> !Objects.equals(mentor, mentee));
        assertTrue(validationResult);

        validationResult = mentorshipValidation.canAddMentorship(mentorId,
                mentorId,
                (mentor, mentee) -> !Objects.equals(mentor, mentee));
        assertFalse(validationResult);

        User mentor = new User();
        mentor.setId(mentorId);

        User mentee = new User();
        mentee.setId(menteeId);
        mentee.setMentors(new ArrayList<>());

        when(mentorshipRepository.getByIdOrThrow(mentorId)).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);

        mentorshipService.addMentorship(mentorId, menteeId);
        verify(mentorshipRepository).save(any());
        verify(mentorshipRepository).save(eq(mentee));
        verify(mentorshipRepository, times(1)).save(eq(mentee));

        boolean isMentorshipExist = mentee.getMentors().contains(mentor);
        assertTrue(isMentorshipExist);
    }

    @Test
    public void testMentorshipDeleted() {
        long currentUserId = 123L;

        when(userContext.getUserId()).thenReturn(currentUserId);
        assertThrows(ForbiddenException.class,
                () -> mentorshipService.deleteMentorship(1L, 2L),
                "Must be exception deleting not yours mentorship relationship");

        long mentorId = 1L;
        User mentor = new User();
        mentor.setId(mentorId);

        long menteeId = 2L;
        User mentee = new User();
        mentee.setId(menteeId);
        List<User> mentors = new ArrayList<>();
        mentors.add(mentor);
        mentee.setMentors(mentors);

        when(mentorshipRepository.getByIdOrThrow(mentorId)).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);
        when(userContext.getUserId()).thenReturn(menteeId);

        mentorshipService.deleteMentorship(menteeId, mentorId);
        verify(mentorshipRepository).save(any());
        verify(mentorshipRepository).save(eq(mentee));
        verify(mentorshipRepository, times(1)).save(eq(mentee));

        boolean isMentorshipExists = mentee.getMentors().contains(mentor);
        assertFalse(isMentorshipExists);
    }

    @Test
    public void testMentorsGet() {
        long mentorId = 2L;
        long menteeId = 3L;

        User mentor = new User();
        mentor.setId(mentorId);

        User mentee = new User();
        mentee.setId(menteeId);
        List<User> mentors = new ArrayList<>();
        mentors.add(mentor);
        mentee.setMentors(mentors);

        boolean isMentorIncluded = mentee.getMentors().contains(mentor);
        assertTrue(isMentorIncluded);

        List<UserDto> expectedUsers = List.of(userMapper.toUserDto(mentor));
        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);

        List<UserDto> result = mentorshipService.getMentors(menteeId);
        assertNotNull(result);
        assertEquals(expectedUsers, result);
    }

    @Test
    public void testMenteesGet() {
        long mentorId = 2L;
        long menteeId = 3L;

        User mentee = new User();
        mentee.setId(menteeId);

        User mentor = new User();
        mentor.setId(mentorId);
        List<User> mentees = new ArrayList<>();
        mentees.add(mentee);
        mentor.setMentees(mentees);

        boolean isMenteeIncluded = mentor.getMentees().contains(mentee);
        assertTrue(isMenteeIncluded);

        List<UserDto> expectedUsers = List.of(userMapper.toUserDto(mentee));
        when(mentorshipRepository.getByIdOrThrow(mentorId)).thenReturn(mentor);

        List<UserDto> result = mentorshipService.getMentees(mentorId);
        assertNotNull(result);
        assertEquals(expectedUsers, result);
    }
}
