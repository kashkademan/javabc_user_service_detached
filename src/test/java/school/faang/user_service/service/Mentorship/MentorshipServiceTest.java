package school.faang.user_service.service.Mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.mentorship.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserValidator userValidator;
    @Spy
    private UserMapperImpl userMapper;

    private final long mentorId = 1L;
    private final long menteeId = 5L;
    private final String mentorName = "Kirill";
    private final String menteeName = "Andy";

    @Test
    public void testGetMenteesNull() {
        when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(prepareUser(menteeId, menteeName, Collections.emptyList(), Collections.emptyList())));
        List<UserDto> result = mentorshipService.getMentees(menteeId);

        assertEquals(0, result.size());
    }

    @Test
    public void testGetMenteesReturnsMenteesList() {
        when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(prepareUser(menteeId, menteeName, prepareUserList(), Collections.emptyList())));
        List<UserDto> result = mentorshipService.getMentees(menteeId);

        assertEquals(prepareUserList().size(), result.size());
        assertEquals(prepareUserList().get(0).getId(), result.get(0).getId());
        assertEquals(prepareUserList().get(1).getUsername(), result.get(1).getUsername());
    }

    @Test
    public void testGetMentorsNull() {
        when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(prepareUser(mentorId, mentorName, Collections.emptyList(), Collections.emptyList())));
        List<UserDto> result = mentorshipService.getMentors(mentorId);

        assertEquals(0, result.size());
    }

    @Test
    public void testGetMentorsReturnsMentorsList() {
        when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(prepareUser(mentorId, mentorName, Collections.emptyList(), prepareUserList())));
        List<UserDto> result = mentorshipService.getMentors(mentorId);

        assertEquals(prepareUserList().size(), result.size());
        assertEquals(prepareUserList().get(0).getId(), result.get(0).getId());
        assertEquals(prepareUserList().get(1).getUsername(), result.get(1).getUsername());
    }

    @Test
    public void testGetUserOrThrow() {
        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> mentorshipService.deleteMentee(menteeId, mentorId));
    }

    @Test
    public void testGetMenteesIsEmpty() {
        when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(
                        prepareUser(menteeId, menteeName, Collections.emptyList(), Collections.emptyList())));
        when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(
                        prepareUser(mentorId, mentorName, Collections.emptyList(), Collections.emptyList())));

        assertThrows(DataValidationException.class, () -> mentorshipService.deleteMentee(menteeId, mentorId));
    }

    @Test
    public void testRemoveMenteeThrow() {
        when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(
                        prepareUser(menteeId, menteeName, Collections.emptyList(), Collections.emptyList())));
        when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(
                        prepareUser(mentorId, mentorName, prepareUserList(), prepareUserList())));

        ResponseEntity<Void> response = mentorshipService.deleteMentee(menteeId, mentorId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testRemoveMentorThrow() {
        when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(
                        prepareUser(menteeId, menteeName, prepareUserList(), prepareUserList())));
        when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(
                        prepareUser(mentorId, mentorName, Collections.emptyList(), Collections.emptyList())));

        ResponseEntity<Void> response = mentorshipService.deleteMentor(menteeId, mentorId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void deleteMentee_whenMentorAndMenteeExist_shouldReturnNoContent() {
        when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(
                        prepareUser(menteeId, menteeName, Collections.emptyList(), prepareUserListCustom(mentorId, mentorName))));
        when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(
                        prepareUser(mentorId, mentorName, prepareUserListCustom(menteeId, menteeName), Collections.emptyList())));

        ResponseEntity<Void> response = mentorshipService.deleteMentee(menteeId, mentorId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void deleteMentor_whenMenteeAndMenteeExist_shouldReturnNoContent() {
        when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(
                        prepareUser(menteeId, menteeName, Collections.emptyList(), prepareUserListCustom(mentorId, mentorName))));
        when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(
                        prepareUser(mentorId, mentorName, prepareUserListCustom(menteeId, menteeName), Collections.emptyList())));

        ResponseEntity<Void> response = mentorshipService.deleteMentor(menteeId, mentorId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    private User prepareUser(long userId, String nameUser, List<User> mentees, List<User> mentors) {
        return User.builder()
                .id(userId)
                .username(nameUser)
                .mentees(mentees)
                .mentors(mentors)
                .build();
    }

    private List<User> prepareUserList() {
        return new ArrayList<>(List.of(
                User.builder().id(2L).username("Viktor").build(),
                User.builder().id(3L).username("Vika").build(),
                User.builder().id(4L).username("Artem").build()
        ));
    }

    private List<User> prepareUserListCustom(long userId, String userName) {
        return new ArrayList<>(List.of(
                User.builder().id(userId).username(userName).build()
        ));
    }
}