package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    private long mentorId = 1L;
    private long menteeId = 2L;
    private User mentor;
    private User mentee;
    private UserDto mentorDto;
    private UserDto menteeDto;

    @BeforeEach
    void setUp() {
        mentor = new User();
        mentor.setId(mentorId);
        mentor.setMentees(new ArrayList<>());

        mentee = new User();
        mentee.setId(menteeId);
        mentee.setMentors(new ArrayList<>());

        mentorDto = new UserDto(
                mentorId,
                "mentor_user",
                "mentor@example.com",
                "+123456789",
                "About mentor");
        menteeDto = new UserDto(menteeId,
                "mentee_user",
                "mentee@example.com",
                "+987654321",
                "About mentee");
    }

    @Test
    void addMentorship_Success() {
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(mentee));

        mentorshipService.addMentorship(mentorId, menteeId);

        verify(mentorshipRepository).save(mentor);
        verify(mentorshipRepository).save(mentee);

        assertTrue(mentor.getMentees().contains(mentee));
    }

    @Test
    void addMentorshipSameIdsThrowsExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> mentorshipService.addMentorship(mentorId, mentorId));
    }

    @Test
    void addMentorship_MentorNotFound() {
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> {
            mentorshipService.addMentorship(mentorId, menteeId);
        });
        verify(mentorshipRepository, never()).save(any(User.class));
    }

    @Test
    void addMentorship_MenteeNotFound() {
        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> {
            mentorshipService.addMentorship(mentorId, menteeId);
        });
        verify(mentorshipRepository, never()).save(any(User.class));
    }

    @Test
    void getMenteesTest() {
        User mentor = new User();
        mentor.setId(mentorId);
        mentor.setMentees(List.of(mentee));

        when(userRepository.existsById(mentorId)).thenReturn(true);
        when(mentorshipRepository.getByIdOrThrow(mentorId)).thenReturn(mentor);
        when(userMapper.toUserDto(mentee)).thenReturn(menteeDto);

        List<UserDto> result = mentorshipService.getMentees(mentorId);

        assertEquals(1, result.size());
        assertEquals(menteeId, result.get(0).id());
    }

    @Test
    void getMenteesThrowsExceptionTest() {
        when(userRepository.existsById(mentorId)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(mentorId));
    }

    @Test
    void getMentorsTest() {
        User mentee = new User();
        mentee.setId(menteeId);
        mentee.setMentors(List.of(mentor));

        when(userRepository.existsById(menteeId)).thenReturn(true);
        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);
        when(userMapper.toUserDto(mentor)).thenReturn(mentorDto);

        List<UserDto> result = mentorshipService.getMentors(menteeId);

        assertEquals(1, result.size());
        assertEquals(mentorId, result.get(0).id()); // Используем id() вместо getId()
    }

    @Test
    void getMentorsThrowsExceptionTest() {
        when(userRepository.existsById(menteeId)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentors(menteeId));
    }

    @Test
    void deleteMentorshipThrowsExceptionTest() {
        User mentee = mock(User.class);

        when(mentorshipRepository.getByIdOrThrow(menteeId)).thenReturn(mentee);
        when(mentee.getMentors()).thenReturn(Collections.emptyList());

        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentorship(menteeId, mentorId));
    }

    @Test
    void deleteMentorshipSameIdsThrowsExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentorship(mentorId, mentorId));
    }
}