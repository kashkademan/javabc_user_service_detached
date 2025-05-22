package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteeMapperImpl;
import school.faang.user_service.mapper.MentorMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceImplTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private MenteeMapperImpl menteeMapper;

    @Spy
    private MentorMapperImpl mentorMapper;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    private static final long USER_MENTOR_ID = 1L;
    private static final long USER_MENTEE_ID = 2L;
    private static final String USER_MENTOR_USERNAME = "Max";
    private static final String USER_MENTEE_USERNAME = "Leo";

    private User userMentor;
    private User userMentee;
    private MenteeDto menteeDto;
    private MentorDto mentorDto;

    @BeforeEach
    void setup() {
        userMentor = User.builder()
                .id(USER_MENTOR_ID)
                .username(USER_MENTOR_USERNAME)
                .mentees(new ArrayList<>())
                .build();
        userMentee = User.builder()
                .id(USER_MENTEE_ID)
                .username(USER_MENTEE_USERNAME)
                .mentors(new ArrayList<>())
                .build();

        menteeDto = new MenteeDto();
        mentorDto = new MentorDto();
    }

    @Test
    public void testGetMentees_whenUserNotFound_shouldThrowException() {
        when(mentorshipRepository.findById(USER_MENTOR_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(USER_MENTOR_ID));
    }

    @Test
    public void testGetMentees_whenMenteesListIsEmpty_shouldReturnEmptyList() {
        userMentor.setMentees(Collections.emptyList());
        when(mentorshipRepository.findById(USER_MENTOR_ID)).thenReturn(Optional.of(userMentor));

        List<MenteeDto> result = mentorshipService.getMentees(USER_MENTOR_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMentees_whenMenteesListNotEmpty_shouldReturnMenteeList() {
        userMentor.setMentees(List.of(userMentee));
        menteeDto.setUsername(userMentee.getUsername());

        when(mentorshipRepository.findById(USER_MENTOR_ID)).thenReturn(Optional.of(userMentor));

        List<MenteeDto> result = mentorshipService.getMentees(USER_MENTOR_ID);
        verify(menteeMapper).toDto(userMentee);

        assertEquals(List.of(menteeDto), result);
    }

    @Test
    public void testGetMentors_whenUserNotFound_shouldThrowException() {
        when(mentorshipRepository.findById(USER_MENTEE_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(USER_MENTEE_ID));
    }

    @Test
    public void testGetMentors_whenMentorsListIsEmpty_shouldReturnEmptyList() {
        userMentee.setMentors(Collections.emptyList());
        when(mentorshipRepository.findById(USER_MENTEE_ID)).thenReturn(Optional.of(userMentee));

        List<MentorDto> result = mentorshipService.getMentors(USER_MENTEE_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMentors_whenMentorsListNotEmpty_shouldReturnMentorList() {
        userMentee.setMentors(List.of(userMentor));
        mentorDto.setUsername(userMentor.getUsername());

        when(mentorshipRepository.findById(USER_MENTEE_ID)).thenReturn(Optional.of(userMentee));

        List<MentorDto> result = mentorshipService.getMentors(USER_MENTEE_ID);
        verify(mentorMapper).toDto(userMentor);

        assertEquals(List.of(mentorDto), result);
    }

    @Test
    public void testDeleteMentorship_whenUsersExistAndAreLinked_shouldRemoveEachOther() {
        userMentor.setMentees(new ArrayList<>(List.of(userMentee)));
        userMentee.setMentors(new ArrayList<>(List.of(userMentor)));

        when(mentorshipRepository.findById(USER_MENTOR_ID)).thenReturn(Optional.of(userMentor));
        when(mentorshipRepository.findById(USER_MENTEE_ID)).thenReturn(Optional.of(userMentee));

        mentorshipService.deleteMentorship(USER_MENTOR_ID, USER_MENTEE_ID);

        verify(mentorshipRepository).save(userMentor);
        verify(mentorshipRepository).save(userMentee);

        assertFalse(userMentee.getMentors().contains(userMentor));
        assertFalse(userMentor.getMentees().contains(userMentee));
    }

    @Test
    public void testDeleteMentorship_whenMentorNotFound_shouldThrowException() {
        userMentor.setMentees(List.of(userMentee));
        userMentee.setMentors(List.of(userMentor));

        when(mentorshipRepository.findById(USER_MENTOR_ID)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentorship(USER_MENTOR_ID, USER_MENTEE_ID
                ));
    }

    @Test
    public void testDeleteMentorship_whenMenteeNotFound_shouldThrowException() {
        userMentor.setMentees(List.of(userMentee));
        userMentee.setMentors(List.of(userMentor));

        when(mentorshipRepository.findById(USER_MENTOR_ID)).thenReturn(Optional.of(userMentor));
        when(mentorshipRepository.findById(USER_MENTEE_ID)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentorship(USER_MENTOR_ID, USER_MENTEE_ID
                ));
    }

    @Test
    public void testDeleteMentorship_whenUsersNotLinked_shouldDoNothing() {
        userMentor.setMentees(new ArrayList<>());
        userMentee.setMentors(new ArrayList<>());

        when(mentorshipRepository.findById(USER_MENTOR_ID)).thenReturn(Optional.of(userMentor));
        when(mentorshipRepository.findById(USER_MENTEE_ID)).thenReturn(Optional.of(userMentee));

        mentorshipService.deleteMentorship(USER_MENTOR_ID, USER_MENTEE_ID);

        verify(mentorshipRepository).save(userMentor);
        verify(mentorshipRepository).save(userMentee);
    }
}