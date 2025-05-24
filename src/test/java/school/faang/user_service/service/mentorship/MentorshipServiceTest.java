package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    private static final long MENTOR_ONE_ID = 1L;
    private static final long MENTOR_TWO_ID = 2L;
    private static final long MENTEE_ONE_ID = 11L;
    private static final long MENTEE_TWO_ID = 22L;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    private User mentorOne;
    private User mentorTwo;
    private User menteeOne;
    private User menteeTwo;

    @BeforeEach
    void setUp() {
        mentorOne = new User();
        mentorTwo = new User();
        menteeOne = new User();
        menteeTwo = new User();
        mentorOne.setId(MENTOR_ONE_ID);
        mentorTwo.setId(MENTOR_TWO_ID);
        menteeOne.setId(MENTEE_ONE_ID);
        menteeTwo.setId(MENTEE_TWO_ID);
    }

    @Test
    void testGetMenteesWhenMentorIsDatabase() {
        mentorOne.setMentees(List.of(menteeOne, menteeTwo));

        when(userRepository.findById(MENTOR_ONE_ID)).thenReturn(Optional.of(mentorOne));

        List<User> actual = mentorshipService.getMentees(MENTOR_ONE_ID);
        List<User> expected = List.of(menteeOne, menteeTwo);

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(MENTOR_ONE_ID);
    }

    @Test
    void testGetMenteesWhenMentorIsNotDatabase() {
        mentorOne.setMentees(List.of(menteeOne, menteeTwo));

        when(userRepository.findById(MENTOR_ONE_ID)).thenThrow(
                new EntityNotFoundException("User does not exist in the database"));

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(MENTOR_ONE_ID));
        verify(userRepository, times(1)).findById(MENTOR_ONE_ID);
    }

    @Test
    void testGetMenteesWhenListMenteesIsEmpty() {
        mentorOne.setMentees(Collections.emptyList());

        when(userRepository.findById(MENTOR_ONE_ID)).thenReturn(Optional.of(mentorOne));

        List<User> actual = mentorshipService.getMentees(MENTOR_ONE_ID);
        List<User> expected = Collections.emptyList();

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(MENTOR_ONE_ID);
    }


    @Test
    void testGetMentorsWhenMenteeIsDatabase() {
        menteeOne.setMentees(List.of(mentorOne, mentorTwo));

        when(userRepository.findById(MENTEE_ONE_ID)).thenReturn(Optional.of(menteeOne));

        List<User> actual = mentorshipService.getMentees(MENTEE_ONE_ID);
        List<User> expected = List.of(mentorOne, mentorTwo);

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(MENTEE_ONE_ID);
    }

    @Test
    void testGetMentorsWhenMenteeIsNotDatabase() {
        menteeOne.setMentees(List.of(mentorOne, mentorTwo));
        when(userRepository.findById(MENTEE_ONE_ID)).thenThrow(
                new EntityNotFoundException("User does not exist in the database"));

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(MENTEE_ONE_ID));
        verify(userRepository, times(1)).findById(MENTEE_ONE_ID);
    }

    @Test
    void testGetMentorsWhenListMentorsIsEmpty() {
        menteeOne.setMentors(Collections.emptyList());

        when(userRepository.findById(MENTEE_ONE_ID)).thenReturn(Optional.of(menteeOne));

        List<User> actual = mentorshipService.getMentors(MENTEE_ONE_ID);
        List<User> expected = Collections.emptyList();

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(MENTEE_ONE_ID);
    }

    @Test
    void testDeleteMenteeWhenMentorAndMenteeHasIsDatabase() {
        mentorOne.setMentees(new ArrayList<>(List.of(menteeOne, menteeTwo)));

        when(mentorshipRepository.findById(MENTOR_ONE_ID)).thenReturn(Optional.of(mentorOne));
        when(mentorshipRepository.findById(MENTEE_ONE_ID)).thenReturn(Optional.of(menteeOne));

        mentorshipService.deleteMentee(MENTOR_ONE_ID, MENTEE_ONE_ID);

        List<User> actual = mentorOne.getMentees();
        List<User> expected = List.of(menteeTwo);

        assertEquals(expected, actual);
        verify(userRepository, times(1)).save(mentorOne);
    }

    @Test
    void testDeleteMenteeWhenMentorNotHasIsDatabase() {
        when(mentorshipRepository.findById(MENTOR_ONE_ID))
                .thenThrow(new EntityNotFoundException("User does not exist in the database"));

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(MENTOR_ONE_ID, MENTEE_ONE_ID));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteMenteeWhenMenteeNotHasIsDatabase() {
        mentorOne.setMentees(new ArrayList<>(List.of(menteeTwo)));

        when(mentorshipRepository.findById(MENTOR_ONE_ID)).thenReturn(Optional.of(mentorOne));
        when(mentorshipRepository.findById(MENTEE_ONE_ID))
                .thenThrow(new EntityNotFoundException("User does not exist in the database"));

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(MENTOR_ONE_ID, MENTEE_ONE_ID));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteMentorWhenMentorAndMenteeHasIsDatabase() {
        menteeOne.setMentors(new ArrayList<>(List.of(mentorOne, mentorTwo)));

        when(mentorshipRepository.findById(MENTOR_ONE_ID)).thenReturn(Optional.of(mentorOne));
        when(mentorshipRepository.findById(MENTEE_ONE_ID)).thenReturn(Optional.of(menteeOne));

        mentorshipService.deleteMentor(MENTOR_ONE_ID, MENTEE_ONE_ID);

        List<User> actual = menteeOne.getMentors();
        List<User> expected = List.of(mentorTwo);

        assertEquals(expected, actual);
        verify(userRepository, times(1)).save(menteeOne);
    }

    @Test
    void testDeleteMentorWhenMentorNotHasIsDatabase() {
        menteeOne.setMentors(new ArrayList<>(List.of(mentorTwo)));

        when(mentorshipRepository.findById(MENTOR_ONE_ID))
                .thenThrow(new EntityNotFoundException("User does not exist in the database"));

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(MENTOR_ONE_ID, MENTEE_ONE_ID));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteMentorWhenMenteeNotHasIsDatabase() {
        when(mentorshipRepository.findById(MENTOR_ONE_ID)).thenReturn(Optional.of(mentorOne));
        when(mentorshipRepository.findById(MENTEE_ONE_ID))
                .thenThrow(new EntityNotFoundException("User does not exist in the database"));

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(MENTOR_ONE_ID, MENTEE_ONE_ID));
        verify(userRepository, never()).save(any());
    }
}