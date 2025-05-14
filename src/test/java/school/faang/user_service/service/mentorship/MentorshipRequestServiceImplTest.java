package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceImplTest {
    private static final long MENTORSHIP_REQUEST_ID = 1L;
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private MentorshipRequestFilter firstFilter;

    @Mock
    private MentorshipRequestFilter secondFilter;

    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    @Test
    public void testRejectRequestDtoNotFound() {
        when(mentorshipRequestRepository.findById(any()))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.rejectRequest(MENTORSHIP_REQUEST_ID, RejectionDto.builder().build()));
    }

    @Test
    public void testRejectRequest() {
        when(mentorshipRequestRepository.findById(any()))
                .thenReturn(Optional.of(MentorshipRequest.builder().build()));

        mentorshipRequestService.rejectRequest(MENTORSHIP_REQUEST_ID, RejectionDto.builder().build());

        verify(mentorshipRequestRepository, times(1)).save(any());
    }

    @Test
    public void testGetRequestsPassAllFilters() {
        MentorshipRequest firstRequest = MentorshipRequest.builder().id(0L).build();
        List<MentorshipRequest> requestsList = List.of(
                firstRequest,
                MentorshipRequest.builder().id(MENTORSHIP_REQUEST_ID).build(),
                MentorshipRequest.builder().id(2L).build()
        );
        when(mentorshipRequestRepository.findAll()).thenReturn(requestsList);
        when(firstFilter.isApplicable(any())).thenReturn(true);
        when(secondFilter.isApplicable(any())).thenReturn(true);
        when(firstFilter.apply(any(), any())).thenAnswer(invocation -> Stream.of(firstRequest));
        when(secondFilter.apply(any(), any())).thenAnswer(invocation -> Stream.of(firstRequest));
        mentorshipRequestService = new MentorshipRequestServiceImpl(
                mentorshipRequestRepository,
                userService,
                mentorshipRequestMapper,
                List.of(firstFilter, secondFilter)
        );

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(RequestFilterDto.builder().build());

        assertEquals(1, result.size());
        assertEquals(0L, result.get(0).getId());
    }

    @Test
    public void testGetRequestsNotPassFilters() {
        MentorshipRequest firstRequest = MentorshipRequest.builder().id(0L).build();
        List<MentorshipRequest> requestsList = List.of(
                firstRequest,
                MentorshipRequest.builder().id(MENTORSHIP_REQUEST_ID).build(),
                MentorshipRequest.builder().id(2L).build()
        );
        when(mentorshipRequestRepository.findAll()).thenReturn(requestsList);
        when(firstFilter.isApplicable(any())).thenReturn(true);
        when(secondFilter.isApplicable(any())).thenReturn(true);
        when(firstFilter.apply(any(), any())).thenAnswer(invocation -> Stream.of(requestsList));
        when(secondFilter.apply(any(), any())).thenAnswer(invocation -> Stream.empty());
        mentorshipRequestService = new MentorshipRequestServiceImpl(
                mentorshipRequestRepository,
                userService,
                mentorshipRequestMapper,
                List.of(firstFilter, secondFilter)
        );

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(RequestFilterDto.builder().build());

        assertEquals(0, result.size());
    }

    @Test
    void testAcceptRequestAlreadyMentorThrowsException() {
        Long requestId = MENTORSHIP_REQUEST_ID;
        Long requesterId = REQUESTER_ID;
        Long receiverId = RECEIVER_ID;
        UserDto receiver = UserDto.builder().id(receiverId).build();
        UserDto requester = UserDto.builder()
                .id(requesterId)
                .mentors(new ArrayList<>(List.of(receiver)))
                .build();
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .id(requestId)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();
        when(mentorshipRequestRepository.findById(requestId))
                .thenReturn(Optional.of(new MentorshipRequest()));
        when(mentorshipRequestMapper.toMentorshipRequestDto(any())).thenReturn(requestDto);
        when(userService.findUserById(requesterId)).thenReturn(requester);
        when(userService.findUserById(receiverId)).thenReturn(receiver);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.acceptRequest(requestId)
        );

        assertEquals("User with id %d is already mentor for user with id %d".formatted(receiverId, requesterId), ex.getMessage());
        verify(userService, never()).updateUser(any());
    }

    @Test
    void testAcceptRequestSuccess() {
        Long requestId = MENTORSHIP_REQUEST_ID;
        Long requesterId = REQUESTER_ID;
        Long receiverId = RECEIVER_ID;
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .id(requestId)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();
        UserDto requester = UserDto.builder()
                .id(requesterId)
                .mentors(new ArrayList<>())
                .build();
        UserDto receiver = UserDto.builder()
                .id(receiverId)
                .build();
        MentorshipRequest entity = MentorshipRequest.builder()
                .id(requestId)
                .status(RequestStatus.ACCEPTED)
                .build();
        when(mentorshipRequestRepository.findById(requestId))
                .thenReturn(Optional.of(new MentorshipRequest()));
        when(mentorshipRequestMapper.toMentorshipRequestDto(any()))
                .thenReturn(requestDto);
        when(userService.findUserById(requesterId)).thenReturn(requester);
        when(userService.findUserById(receiverId)).thenReturn(receiver);
        when(mentorshipRequestMapper.toMentorshipRequestEntity(any())).thenReturn(entity);
        when(mentorshipRequestRepository.save(entity)).thenReturn(entity);
        when(mentorshipRequestMapper.toMentorshipRequestDto(entity)).thenReturn(requestDto);

        MentorshipRequestDto result = mentorshipRequestService.acceptRequest(requestId);

        assertEquals(RequestStatus.ACCEPTED, result.getRequestStatus());
        verify(userService).updateUser(requester);
        assertTrue(requester.getMentors().contains(receiver));
    }

    @Test
    void testRequestMentorshipTooFrequentThrows() {
        Long requesterId = REQUESTER_ID;
        Long receiverId = RECEIVER_ID;
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();
        UserDto requester = UserDto.builder().id(requesterId).build();
        UserDto receiver = UserDto.builder().id(receiverId).build();
        MentorshipRequest recentRequest = MentorshipRequest.builder()
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
        when(userService.findUserById(requesterId)).thenReturn(requester);
        when(userService.findUserById(receiverId)).thenReturn(receiver);
        when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(recentRequest));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(dto)
        );

        assertEquals("User 1 can only create request once in 3 month", ex.getMessage());
        verify(mentorshipRequestRepository, never()).save(any());
    }

    @Test
    void testRequestMentorshipSelfRequestThrows() {
        Long userId = REQUESTER_ID;
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .requesterId(userId)
                .receiverId(userId)
                .build();
        UserDto user = UserDto.builder().id(userId).build();
        when(userService.findUserById(userId)).thenReturn(user);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(dto)
        );

        assertEquals("User with id %d could not send mentorship request to himself".formatted(userId), ex.getMessage());
        verify(mentorshipRequestRepository, never()).save(any());
    }

    @Test
    void testRequestMentorshipSuccess() {
        Long requesterId = REQUESTER_ID;
        Long receiverId = RECEIVER_ID;
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();
        UserDto requester = UserDto.builder().id(requesterId).build();
        UserDto receiver = UserDto.builder().id(receiverId).build();
        MentorshipRequest entity = mentorshipRequestMapper.toMentorshipRequestEntity(dto);
        when(userService.findUserById(requesterId)).thenReturn(requester);
        when(userService.findUserById(receiverId)).thenReturn(receiver);
        when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.empty());
        when(mentorshipRequestRepository.save(entity)).thenReturn(entity);

        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(dto);

        verify(mentorshipRequestRepository).save(entity);
    }
}