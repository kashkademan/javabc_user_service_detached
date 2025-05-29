
package school.faang.user_service.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.GetMenteesResponseDto;
import school.faang.user_service.dto.mentorship.GetMentorsResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MenteeMapper;
import school.faang.user_service.mapper.mentorship.MentorMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private MenteeMapper menteeMapper;

    @Spy
    private MentorMapper mentorsMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void testGetMentees() {
        User miras = createUser(1L, "Miras");
        User jon = createUser(2L, "Jon");
        List<User> mentees = new ArrayList<>();
        mentees.add(jon);
        miras.setMentees(mentees);
        List<GetMenteesResponseDto> menteeList = mentees.stream()
                .map(user -> menteeMapper.toDto(user)).toList();

        when(mentorshipRepository.findById(miras.getId()))
                .thenReturn(Optional.of(miras));

        List<GetMenteesResponseDto> result = mentorshipService.getMentees(miras.getId());

        assertNotNull(result);
        assertEquals(menteeList, result);
    }

    @Test
    public void testGetMenteesNotFound() {
        assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.getMentees(1L)
        );
    }

    @Test
    public void testGetMentors() {
        User miras = createUser(1L, "Miras");
        User jon = createUser(2L, "Jon");
        List<User> mentors = new ArrayList<>();
        mentors.add(miras);
        jon.setMentors(mentors);
        List<GetMentorsResponseDto> mentorList = mentors.stream()
                .map(mentorsMapper::toDto).toList();

        when(mentorshipRepository.findById(jon.getId()))
                .thenReturn(Optional.of(jon));

        List<GetMentorsResponseDto> result = mentorshipService.getMentors(jon.getId());

        assertNotNull(result);
        assertEquals(mentorList, result);
    }

    @Test
    public void testGetMentorsNotFound() {
        assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.getMentors(1L)
        );
    }

    @Test
    public void testDeleteMentee() {
        User miras = createUser(1L, "Miras");
        User jon = createUser(2L, "Jon");
        List<User> mentees = new ArrayList<>();
        List<User> mentors = new ArrayList<>();
        mentees.add(jon);
        mentors.add(miras);
        miras.setMentees(mentees);
        jon.setMentors(mentors);

        when(mentorshipRepository.findById(miras.getId()))
                .thenReturn(Optional.of(miras));
        when(mentorshipRepository.findById(jon.getId()))
                .thenReturn(Optional.of(jon));

        mentorshipService.deleteMentee(2L, 1L);

        verify(mentorshipRepository, times(1)).save(jon);
        verify(mentorshipRepository, times(1)).save(miras);
    }

    @Test
    public void testDeleteMentor() {
        User miras = createUser(1L, "Miras");
        User jon = createUser(2L, "Jon");
        List<User> mentees = new ArrayList<>();
        List<User> mentors = new ArrayList<>();
        mentees.add(jon);
        mentors.add(miras);
        miras.setMentees(mentees);
        jon.setMentors(mentors);

        when(mentorshipRepository.findById(miras.getId()))
                .thenReturn(Optional.of(miras));
        when(mentorshipRepository.findById(jon.getId()))
                .thenReturn(Optional.of(jon));

        mentorshipService.deleteMentor(2L, 1L);

        verify(mentorshipRepository, times(1)).save(miras);
        verify(mentorshipRepository, times(1)).save(jon);
    }

    private User createUser(long id, String username) {
        return User.builder().id(id).username(username).build();
    }
}
