package school.faang.user_service.service.mentorship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validation.mentorship.MentorshipValidationImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    UserContext userContext;
    @Mock
    MentorshipRepository mentorshipRepository;
    @Spy
    MentorshipValidationImpl mentorshipValidation;
    @Spy
    UserMapperImpl userMapper;
    @InjectMocks
    MentorshipServiceImpl mentorshipService;

    private User mentor;
    private User mentee;
    List<UserDto> actualResult;
    UserDto actualUser;
    boolean validationResult;

    @BeforeEach
    void prepareUsers() {
        userContext.setUserId(123L);
        mentor = User.builder()
                .id(1L)
                .mentees(new ArrayList<>())
                .build();
        mentee = User.builder()
                .id(2L)
                .mentors(new ArrayList<>())
                .build();
        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);
        actualResult = new ArrayList<>();
        actualUser = null;
    }

    @Test
    void testExistedMentorshipAdded() {
        when(mentorshipRepository.getByIdOrThrow(mentor.getId())).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(mentee.getId())).thenReturn(mentee);
        actualUser = mentorshipService.addMentorship(mentor.getId(), mentee.getId());
        assertEquals(userMapper.toUserDto(mentee), actualUser);
    }

    @Test
    void testSuccessfullyMentorshipAdded() {
        mentor.setMentees(new ArrayList<>());
        mentee.setMentors(new ArrayList<>());
        when(mentorshipRepository.getByIdOrThrow(mentor.getId())).thenReturn(mentor);
        when(mentorshipRepository.getByIdOrThrow(mentee.getId())).thenReturn(mentee);
        when(mentorshipRepository.save(mentee)).thenReturn(mentee);
        actualUser = mentorshipService.addMentorship(mentor.getId(), mentee.getId());
        assertEquals(userMapper.toUserDto(mentee), actualUser);
    }

    @Test
    void testSuccessfullyValidationInputParamBeforeMentorshipAdded() {
        validationResult = mentorshipValidation.canAddMentorship(mentor.getId(),
                mentee.getId(),
                (mentorId, menteeId) -> !Objects.equals(mentorId, menteeId));
        assertTrue(validationResult);
    }

    @Test
    void testUnsuccessfullyValidationInputParamBeforeMentorshipAdded() {
        validationResult = mentorshipValidation.canAddMentorship(mentor.getId(),
                mentor.getId(),
                (mentorId, menteeId) -> !Objects.equals(mentorId, menteeId));
        assertFalse(validationResult);
    }

    @Test
    void testDataValidationExceptionWhileMentorshipAdd() {
        assertThrows(DataValidationException.class,
                () -> mentorshipService.addMentorship(mentor.getId(), mentor.getId()),
                "mentor Id and mentee Id can't be equal");
    }

    @Test
    void testForbiddenExceptionWhileMentorshipDelete() {
        assertThrows(ForbiddenException.class,
                () -> mentorshipService.deleteMentorship(mentee.getId(), mentor.getId()),
                "Can't delete mentorship");
    }

    @Test
    void testSuccessfullyMentorshipDeleted() {
        when(userContext.getUserId()).thenReturn(mentee.getId());
        when(mentorshipRepository.getByIdOrThrow(mentee.getId())).thenReturn(mentee);
        when(mentorshipRepository.getByIdOrThrow(mentor.getId())).thenReturn(mentor);
        when(mentorshipRepository.save(mentee)).thenReturn(mentee);
        actualUser = mentorshipService.deleteMentorship(mentee.getId(), mentor.getId());
        assertEquals(userMapper.toUserDto(mentee), actualUser);
    }

    @Test
    void testMentorsGet() {
        List<UserDto> expectedMentors = List.of(userMapper.toUserDto(mentor));
        when(mentorshipRepository.getByIdOrThrow(mentee.getId())).thenReturn(mentee);
        actualResult = mentorshipService.getMentors(mentee.getId());
        assertEquals(expectedMentors, actualResult);
    }

    @Test
    void testMenteesGet() {
        List<UserDto> expectedMentees = List.of(userMapper.toUserDto(mentee));
        when(mentorshipRepository.getByIdOrThrow(mentor.getId())).thenReturn(mentor);
        actualResult = mentorshipService.getMentees(mentor.getId());
        assertEquals(expectedMentees, actualResult);
    }


}
