package school.faang.user_service.service.Mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;
    @Captor
    private ArgumentCaptor<MentorshipRequest> requestCaptor;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    private final long requestId = 1L;
    private final long requesterId = 2L;
    private final long receiverId = 3L;
    private final String description = "test";
    private final User mentor = prepareUser(receiverId);
    private final User mentee = prepareUser(requesterId);
    private final MentorshipRequest mentorshipRequest = MentorshipRequest
            .builder()
            .id(requestId)
            .receiver(mentor)
            .requester(mentee)
            .status(RequestStatus.PENDING)
            .build();

    @Test
    public void testRequestMentorshipPerfectCase() {
        when(mentorshipRequestRepository.create(requesterId, receiverId, description))
                .thenReturn(prepareteMentorshipRequest(
                        requestId,
                        prepareUser(requesterId),
                        prepareUser(receiverId),
                        description)
                );
        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(
                prepareMentorshipRequestDto(requesterId, receiverId, description));

        Assertions.assertEquals(requesterId, result.getRequesterId());
        Assertions.assertEquals(receiverId, result.getReceiverId());
        Assertions.assertEquals(description, result.getDescription());
    }

    @Test
    public void testGetMentorshipRequest_DataValidationException() {
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataValidationException.class, () -> mentorshipRequestService.acceptRequest(requestId));
    }

    @Test
    public void testFetchUserOrThrow() {
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(MentorshipRequest
                .builder()
                .id(requestId)
                .receiver(prepareUser(receiverId))
                .build()));
        when(userRepository.findById(receiverId)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataValidationException.class, () -> mentorshipRequestService.acceptRequest(requestId));
    }

    @Test
    public void testAcceptRequest_PerfectCase() {
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(mentor));
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(mentee));

        mentorshipRequestService.acceptRequest(requestId);
        verify(mentorshipRequestRepository, times(1)).save(requestCaptor.capture());
        verify(userRepository, times(2)).save(userCaptor.capture());

        MentorshipRequest resultRequest = requestCaptor.getValue();
        List<User> resultUsers = userCaptor.getAllValues();

        Assertions.assertEquals(RequestStatus.ACCEPTED, resultRequest.getStatus());
        Assertions.assertTrue(mentor.getMentees().stream()
                .anyMatch(m -> m.getId().equals(mentee.getId())), "Mentee должен быть в mentees ментора");
        Assertions.assertTrue(mentee.getMentors().stream()
                .anyMatch(m -> m.getId().equals(mentor.getId())), "Ментор должен быть в mentors ментии");
    }

    @Test
    public void testRejectRequest_PerfectCase() {
        String reason = "test";
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.rejectRequest(requestId, RejectionDto.builder().reason(reason).build());
        verify(mentorshipRequestRepository).save(requestCaptor.capture());
        MentorshipRequest result = requestCaptor.getValue();

        Assertions.assertEquals(RequestStatus.REJECTED, result.getStatus());
        Assertions.assertEquals(reason, result.getRejectionReason());
    }

    private MentorshipRequestDto prepareMentorshipRequestDto(
            long requesterId, long receiverId, String description) {
        return MentorshipRequestDto.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .description(description)
                .build();
    }

    private MentorshipRequest prepareteMentorshipRequest(
            long requestId, User requester, User receiver, String description) {
        return MentorshipRequest.builder()
                .id(requestId)
                .requester(requester)
                .receiver(receiver)
                .description(description)
                .build();
    }

    private User prepareUser(long userId) {
        return User.builder()
                .id(userId)
                .mentees(prepareListUser(5L))
                .mentors(prepareListUser(6L))
                .build();
    }

    private List<User> prepareListUser(long userId) {
        return new ArrayList<>(List.of(
                User.builder().id(userId).build()
        ));
    }
}