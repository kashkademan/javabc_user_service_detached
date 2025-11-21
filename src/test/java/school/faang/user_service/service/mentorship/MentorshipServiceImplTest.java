package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.MentorshipEventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.messages.redis.publishers.MentorshipOfferedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    @Test
    void addMentorship_validIds_shouldAddMentorship() {
        Long mentorId = 1L;
        Long menteeId = 2L;

        when(mentorshipRepository.existsMentorship(mentorId, menteeId)).thenReturn(false);

        mentorshipService.addMentorship(mentorId, menteeId);

        verify(mentorshipRepository, times(1)).addMentorshipNative(mentorId, menteeId);
        verify(mentorshipOfferedEventPublisher, times(1)).sendNotification(any(MentorshipEventDto.class));
    }

    @Test
    void addMentorship_duplicateIds_shouldThrowException() {
        Long mentorId = 1L;
        Long menteeId = 1L;

        assertThrows(DataValidationException.class,
                () -> mentorshipService.addMentorship(mentorId, menteeId));

        verify(mentorshipRepository, never()).addMentorshipNative(anyLong(), anyLong());
    }

    @Test
    void addMentorship_alreadyExists_shouldThrowException() {
        Long mentorId = 1L;
        Long menteeId = 2L;

        when(mentorshipRepository.existsMentorship(mentorId, menteeId)).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.addMentorship(mentorId, menteeId));

        verify(mentorshipRepository, never()).addMentorshipNative(anyLong(), anyLong());
    }

    @Test
    void getMentees_validUserId_shouldReturnMentees() {
        User mentee1 = new User();
        mentee1.setId(2L);
        mentee1.setUsername("mentee1");
        mentee1.setEmail("mentee1@test.com");
        mentee1.setPhone("111111111");
        mentee1.setAboutMe("About mentee1");

        User mentee2 = new User();
        mentee2.setId(3L);
        mentee2.setUsername("mentee2");
        mentee2.setEmail("mentee2@test.com");
        mentee2.setPhone("222222222");
        mentee2.setAboutMe("About mentee2");

        List<User> mentees = List.of(mentee1, mentee2);

        UserDto dto1 = new UserDto(
                2L,
                "mentee1",
                "mentee1@test.com",
                "111111111",
                "About mentee1",
                null,
                List.of()
        );
        UserDto dto2 = new UserDto(
                3L,
                "mentee2",
                "mentee2@test.com",
                "222222222",
                "About mentee2",
                null,
                List.of()
        );

        Long userId = 1L;
        when(mentorshipRepository.getMenteesById(userId)).thenReturn(mentees);
        when(userMapper.toUserDto(mentee1)).thenReturn(dto1);
        when(userMapper.toUserDto(mentee2)).thenReturn(dto2);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertEquals(2, result.size());
        List<UserDto> expectedDtos = List.of(dto1, dto2);
        assertEquals(expectedDtos, result);
        verify(mentorshipRepository, times(1)).getMenteesById(userId);
    }

    @Test
    void getMentors_validUserId_shouldReturnMentors() {
        User mentor1 = new User();
        mentor1.setId(4L);
        mentor1.setUsername("mentor1");
        mentor1.setEmail("mentor1@test.com");
        mentor1.setPhone("123456789");
        mentor1.setAboutMe("About mentor1");

        User mentor2 = new User();
        mentor2.setId(5L);
        mentor2.setUsername("mentor2");
        mentor2.setEmail("mentor2@test.com");
        mentor2.setPhone("987654321");
        mentor2.setAboutMe("About mentor2");

        List<User> mentors = List.of(mentor1, mentor2);

        UserDto dto1 = new UserDto(
                4L,
                "mentor1",
                "mentor1@test.com",
                "123456789",
                "About mentor1",
                null,
                List.of()
        );
        UserDto dto2 = new UserDto(
                5L,
                "mentor2",
                "mentor2@test.com",
                "987654321",
                "About mentor2",
                null,
                List.of()
        );
        Long userId = 1L;
        when(mentorshipRepository.getMentorsById(userId)).thenReturn(mentors);
        when(userMapper.toUserDto(mentor1)).thenReturn(dto1);
        when(userMapper.toUserDto(mentor2)).thenReturn(dto2);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertEquals(2, result.size());
        List<UserDto> expectedDtos = List.of(dto1, dto2);
        assertEquals(expectedDtos, result);
        verify(mentorshipRepository, times(1)).getMentorsById(userId);
    }

    @Test
    void deleteMentorship_currentUserIsMentee_shouldDeleteMentorship() {
        Long mentorId = 1L;
        Long menteeId = 2L;
        Long currentUserId = 2L;

        when(userContext.getUserId()).thenReturn(currentUserId);

        mentorshipService.deleteMentorship(mentorId, menteeId);

        verify(mentorshipRepository, times(1)).deleteMentorshipNative(mentorId, menteeId);
    }

    @Test
    void deleteMentorship_currentUserIsMentor_shouldDeleteMentorship() {
        Long mentorId = 1L;
        Long menteeId = 2L;
        Long currentUserId = 1L;

        when(userContext.getUserId()).thenReturn(currentUserId);

        mentorshipService.deleteMentorship(mentorId, menteeId);

        verify(mentorshipRepository, times(1)).deleteMentorshipNative(mentorId, menteeId);
    }

    @Test
    void deleteMentorship_currentUserNotInvolved_shouldThrowException() {
        Long mentorId = 1L;
        Long menteeId = 2L;
        Long currentUserId = 3L;

        when(userContext.getUserId()).thenReturn(currentUserId);

        assertThrows(ForbiddenException.class,
                () -> mentorshipService.deleteMentorship(mentorId, menteeId));

        verify(mentorshipRepository, never()).deleteMentorshipNative(anyLong(), anyLong());
    }

    @Test
    void deleteMentorship_duplicateIds_shouldThrowException() {
        Long mentorId = 1L;
        Long menteeId = 1L;
        Long currentUserId = 1L;

        when(userContext.getUserId()).thenReturn(currentUserId);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentorship(mentorId, menteeId));

        verify(mentorshipRepository, never()).deleteMentorshipNative(anyLong(), anyLong());
    }

    @Test
    void getMentees_emptyList_shouldReturnEmptyList() {
        Long userId = 1L;

        when(mentorshipRepository.getMenteesById(userId)).thenReturn(List.of());

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertTrue(result.isEmpty());
        verify(mentorshipRepository, times(1)).getMenteesById(userId);
    }

    @Test
    void getMentors_emptyList_shouldReturnEmptyList() {
        Long userId = 1L;

        when(mentorshipRepository.getMentorsById(userId)).thenReturn(List.of());

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertTrue(result.isEmpty());
        verify(mentorshipRepository, times(1)).getMentorsById(userId);
    }
}