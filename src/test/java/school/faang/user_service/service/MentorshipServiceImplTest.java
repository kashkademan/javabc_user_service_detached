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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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

    private final long MENTOR_ID = 1L;
    private final long MENTEE_ID = 2L;
    private User mentor;
    private User mentee;
    private UserDto mentorDto;
    private UserDto menteeDto;

    @BeforeEach
    void setUp() {
        mentor = new User();
        mentor.setId(MENTOR_ID);

        mentee = new User();
        mentee.setId(MENTEE_ID);

        mentorDto = new UserDto(MENTOR_ID, "mentor_user", "mentor@example.com", "+123456789", "About mentor");
        menteeDto = new UserDto(MENTEE_ID, "mentee_user", "mentee@example.com", "+987654321", "About mentee");
    }

    @Test
    void addMentorship_Success() {
        when(mentorshipRepository.existsById(MENTOR_ID)).thenReturn(false);
        when(mentorshipRepository.existsById(MENTEE_ID)).thenReturn(false);
        when(mentorshipRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));

        assertDoesNotThrow(() -> mentorshipService.addMentorship(MENTOR_ID, MENTEE_ID));
        verify(mentorshipRepository, times(1)).save(mentee);
    }

    @Test
    void addMentorship_SameIds_ThrowsException() {
        assertThrows(DataValidationException.class,
                () -> mentorshipService.addMentorship(MENTOR_ID, MENTOR_ID));
    }

    @Test
    void addMentorship_MentorNotFound_ThrowsException() {
        when(mentorshipRepository.existsById(MENTOR_ID)).thenReturn(false);
        when(mentorshipRepository.findById(MENTOR_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> mentorshipService.addMentorship(MENTOR_ID, MENTEE_ID));
    }

    @Test
    void addMentorship_RelationshipExists_ThrowsException() {
        when(mentorshipRepository.existsById(MENTOR_ID)).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.addMentorship(MENTOR_ID, MENTEE_ID));
    }

    @Test
    void getMentees_Success() {
        User mentor = new User();
        mentor.setId(MENTOR_ID);
        mentor.setMentees(List.of(mentee));

        when(userRepository.existsById(MENTOR_ID)).thenReturn(true);
        when(mentorshipRepository.getByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        when(userMapper.toUserDto(mentee)).thenReturn(menteeDto);

        List<UserDto> result = mentorshipService.getMentees(MENTOR_ID);

        assertEquals(1, result.size());
        assertEquals(MENTEE_ID, result.get(0).id());
    }

    @Test
    void getMentees_UserNotFound_ThrowsException() {
        when(userRepository.existsById(MENTOR_ID)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(MENTOR_ID));
    }

    @Test
    void getMentors_Success() {
        User mentee = new User();
        mentee.setId(MENTEE_ID);
        mentee.setMentors(List.of(mentor));

        when(userRepository.existsById(MENTEE_ID)).thenReturn(true);
        when(mentorshipRepository.getByIdOrThrow(MENTEE_ID)).thenReturn(mentee);
        when(userMapper.toUserDto(mentor)).thenReturn(mentorDto);

        List<UserDto> result = mentorshipService.getMentors(MENTEE_ID);

        assertEquals(1, result.size());
        assertEquals(MENTOR_ID, result.get(0).id()); // Используем id() вместо getId()
    }

    @Test
    void getMentors_UserNotFound_ThrowsException() {
        when(userRepository.existsById(MENTEE_ID)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentors(MENTEE_ID));
    }

    @Test
    void deleteMentorship_MentorNotInList_ThrowsException() {
        // Создаем mock для mentee
        User mentee = mock(User.class);

        when(mentorshipRepository.getByIdOrThrow(MENTEE_ID)).thenReturn(mentee);
        when(mentee.getMentors()).thenReturn(Collections.emptyList());

        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentorship(MENTEE_ID, MENTOR_ID));
    }

    @Test
    void deleteMentorship_SameIds_ThrowsException() {
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentorship(MENTOR_ID, MENTOR_ID));
    }
}