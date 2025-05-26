package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.MentorshipResponseDto;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship_request.DescriptionFilter;
import school.faang.user_service.filter.mentorship_request.ReceiverFilter;
import school.faang.user_service.filter.mentorship_request.RequestFilter;
import school.faang.user_service.filter.mentorship_request.RequesterFilter;
import school.faang.user_service.filter.mentorship_request.StatusFilter;
import school.faang.user_service.mapper.RequestToResponseDtoImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Spy
    private RequestToResponseDtoImpl responseMapper;
    @Mock
    private DescriptionFilter descriptionFilter;
    @Mock
    private ReceiverFilter receiverFilter;
    @Mock
    private RequesterFilter requesterFilter;
    @Mock
    private StatusFilter statusFilter;

    @BeforeEach
    public void setUp() {
        mentorshipRequestService = new MentorshipRequestService(
                mentorshipRequestRepository,
                responseMapper,
                List.of(descriptionFilter, receiverFilter, requesterFilter, statusFilter)
        );
    }

    @Test
    void testRequestMentorshipExceptionEqualsIdDto() {
        MentorshipRequestDto dto = new MentorshipRequestDto("asd", 1L, 1L,
                RequestStatus.ACCEPTED, LocalDateTime.now(), LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }


    @Test
    void testRequestMentorshipExceptionThreeMonths() {
        long requesterId = 1L;
        long receiverId = 2L;
        MentorshipRequestDto dto = new MentorshipRequestDto("asd", requesterId, receiverId,
                RequestStatus.ACCEPTED, LocalDateTime.now(), LocalDateTime.now());

        User requester = createUserById(requesterId);
        User receiver = createUserById(receiverId);

        Optional<MentorshipRequest> optional =
                Optional.of(new MentorshipRequest(3L, "qwe", requester, receiver, RequestStatus.ACCEPTED,
                        "",
                        LocalDateTime.of(2025, 1, 1, 1, 1),
                        LocalDateTime.of(2025, 1, 1, 1, 1)));

        when(mentorshipRequestRepository
                .findLatestRequest(dto.requesterId(), dto.receiverId())).thenReturn(optional);

        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    void testRequestMentorship() {
        long requesterId = 1L;
        long receiverId = 2L;
        MentorshipRequestDto dto = new MentorshipRequestDto("asd", requesterId, receiverId,
                RequestStatus.ACCEPTED, LocalDateTime.now(), LocalDateTime.now());

        User requester = createUserById(requesterId);
        User receiver = createUserById(receiverId);

        Optional<MentorshipRequest> optional =
                Optional.of(new MentorshipRequest(3L, "qwe", requester, receiver, RequestStatus.ACCEPTED,
                        "",
                        LocalDateTime.of(2025, 1, 1, 1, 1),
                        LocalDateTime.of(2025, 4, 1, 1, 1)));

        MentorshipRequest mentorshipRequest =
                new MentorshipRequest(1L, "asdf", requester, receiver, RequestStatus.ACCEPTED, "",
                        LocalDateTime.of(2025, 1, 1, 1, 1),
                        LocalDateTime.of(2025, 4, 1, 1, 1));

        MentorshipResponseDto responseDto = new MentorshipResponseDto(1L, "asdf", requesterId, receiverId,
                RequestStatus.ACCEPTED,
                LocalDateTime.of(2025, 1, 1, 1, 1),
                LocalDateTime.of(2025, 4, 1, 1, 1));

        when(mentorshipRequestRepository
                .findLatestRequest(dto.requesterId(), dto.receiverId())).thenReturn(optional);
        when(mentorshipRequestRepository.create(dto.requesterId(), dto.receiverId(), dto.description()))
                .thenReturn(mentorshipRequest);

        MentorshipResponseDto result = mentorshipRequestService.requestMentorship(dto);

        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    void testAcceptRequestNoSuchId() {
        long id = 1L;
        when(mentorshipRequestRepository.findById(id)).thenThrow(new IllegalArgumentException("there is no such id"));

        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(id));
    }

    @Test
    void testAcceptRequestHaveSuchMentor() {
        long idRequest = 1L;
        User requester = createUserById(idRequest);
        User receiver = createUserByIdAndMentees(idRequest, requester);
        MentorshipRequest request = new MentorshipRequest(idRequest, "", requester,
                receiver, RequestStatus.ACCEPTED, "", LocalDateTime.now(), LocalDateTime.now());

        getMentorshipRequest(idRequest, request);

        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(idRequest));
    }

    @Test
    void testAcceptRequest() {
        long id = 1L;
        User requester = createUserByIdAndMentors(id);
        User receiver = createUserByIdAndMentees(id, new User());
        MentorshipRequest request = new MentorshipRequest(id, " ", requester, receiver,
                RequestStatus.ACCEPTED, "", LocalDateTime.now(), LocalDateTime.now());


        getMentorshipRequest(id, request);

        mentorshipRequestService.acceptRequest(id);
        verify(mentorshipRequestRepository, times(1)).save(request);
    }

    @Test
    void rejectRequestRequestAlreadyRejected() {
        long idRequest = 1L;
        MentorshipRequest request = new MentorshipRequest(idRequest, "", null, null,
                RequestStatus.ACCEPTED, "",
                LocalDateTime.now(), LocalDateTime.now());

        getMentorshipRequest(idRequest, request);

        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService
                .rejectRequest(idRequest, new RejectionDto("")));
    }

    @Test
    void rejectRequest() {
        long idRequest = 1L;
        MentorshipRequest request = new MentorshipRequest(idRequest, "", null, null,
                RequestStatus.PENDING, "",
                LocalDateTime.now(), LocalDateTime.now());
        RejectionDto dto = new RejectionDto("test");
        getMentorshipRequest(idRequest, request);

        mentorshipRequestService.rejectRequest(idRequest, dto);

        assertEquals(RequestStatus.REJECTED, request.getStatus());
        assertEquals(dto.reason(), request.getRejectionReason());
    }

    @Test
    void testGetRequestsOneFilter() {
        MentorshipRequest request1 = new MentorshipRequest(1L,
                "",
                User.builder().id(1L).build(),
                User.builder().id(2L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequest request2 = new MentorshipRequest(1L,
                "",
                User.builder().id(3L).build(),
                User.builder().id(4L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequest request3 = new MentorshipRequest(1L,
                "",
                User.builder().id(2L).build(),
                User.builder().id(3L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequestFilterDto dto = new MentorshipRequestFilterDto(null, 1L, null, null);
        List<MentorshipRequest> list = List.of(request1, request2, request3);
        List<MentorshipRequest> list2 = List.of(request1);

        when(mentorshipRequestRepository.findAll()).thenReturn(list);
        filtersReturnTrue(requesterFilter);
        filtersReturnFalse(receiverFilter);
        filtersReturnFalse(statusFilter);
        filtersReturnFalse(descriptionFilter);
        when(requesterFilter.apply(any(), any())).thenReturn(list2.stream());

        List<MentorshipResponseDto> result = mentorshipRequestService.getRequests(dto);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).receiverId());
    }

    @Test
    void testGetRequestsTwoFilter() {
        MentorshipRequest request1 = new MentorshipRequest(1L,
                "test one",
                User.builder().id(1L).build(),
                User.builder().id(2L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequest request2 = new MentorshipRequest(1L,
                "test two",
                User.builder().id(3L).build(),
                User.builder().id(4L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequest request3 = new MentorshipRequest(1L,
                "test three",
                User.builder().id(2L).build(),
                User.builder().id(5L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequestFilterDto dto = new MentorshipRequestFilterDto("test", 1L, null, null);
        List<MentorshipRequest> list = List.of(request1, request2, request3);
        List<MentorshipRequest> list2 = List.of(request1);

        when(mentorshipRequestRepository.findAll()).thenReturn(list);
        filtersReturnTrue(requesterFilter);
        filtersReturnFalse(receiverFilter);
        filtersReturnFalse(statusFilter);
        filtersReturnTrue(descriptionFilter);
        when(requesterFilter.apply(any(), any())).thenReturn(list2.stream());

        List<MentorshipResponseDto> result = mentorshipRequestService.getRequests(dto);

        assertEquals(1, result.size());
        assertEquals("test one", result.get(0).description());
    }

    @Test
    void testGetRequestsThreeFilter() {
        MentorshipRequest request1 = new MentorshipRequest(1L,
                "test one",
                User.builder().id(1L).build(),
                User.builder().id(2L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequest request2 = new MentorshipRequest(1L,
                "test two",
                User.builder().id(3L).build(),
                User.builder().id(4L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequest request3 = new MentorshipRequest(1L,
                "test three",
                User.builder().id(2L).build(),
                User.builder().id(5L).build(),
                RequestStatus.PENDING,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequestFilterDto dto = new MentorshipRequestFilterDto("test", 1L, null, RequestStatus.PENDING);
        List<MentorshipRequest> list = List.of(request1, request2, request3);
        List<MentorshipRequest> list2 = new ArrayList<>();

        when(mentorshipRequestRepository.findAll()).thenReturn(list);
        filtersReturnTrue(requesterFilter);
        filtersReturnFalse(receiverFilter);
        filtersReturnTrue(statusFilter);
        filtersReturnTrue(descriptionFilter);
        when(requesterFilter.apply(any(), any())).thenReturn(list2.stream());

        List<MentorshipResponseDto> result = mentorshipRequestService.getRequests(dto);

        assertEquals(0, result.size());
    }

    @Test
    void testGetRequestsFourFilter() {
        MentorshipRequest request1 = new MentorshipRequest(1L,
                "test one",
                User.builder().id(1L).build(),
                User.builder().id(2L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequest request2 = new MentorshipRequest(1L,
                "test two",
                User.builder().id(3L).build(),
                User.builder().id(4L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequest request3 = new MentorshipRequest(1L,
                "test three",
                User.builder().id(2L).build(),
                User.builder().id(5L).build(),
                RequestStatus.ACCEPTED,
                "",
                LocalDateTime.now(),
                LocalDateTime.now());

        MentorshipRequestFilterDto dto = new MentorshipRequestFilterDto("test", 1L, 2L, RequestStatus.ACCEPTED);
        List<MentorshipRequest> list = List.of(request1, request2, request3);
        List<MentorshipRequest> list2 = List.of(request1);

        when(mentorshipRequestRepository.findAll()).thenReturn(list);
        filtersReturnTrue(requesterFilter);
        filtersReturnTrue(receiverFilter);
        filtersReturnTrue(statusFilter);
        filtersReturnTrue(descriptionFilter);
        when(requesterFilter.apply(any(), any())).thenReturn(list2.stream());

        List<MentorshipResponseDto> result = mentorshipRequestService.getRequests(dto);

        assertEquals(0, result.size());
    }


    private User createUserById(Long userId) {
        return User.builder().id(userId).build();
    }

    private User createUserByIdAndMentees(Long userId, User requester) {
        return User.builder().id(userId).mentees(new ArrayList<>(List.of(requester))).build();
    }

    private User createUserByIdAndMentors(Long userId) {
        return User.builder().id(userId).mentors(new ArrayList<>(List.of(new User()))).build();
    }

    private void getMentorshipRequest(long id, MentorshipRequest request) {
        when(mentorshipRequestRepository.findById(id))
                .thenReturn(Optional.of(request));
    }

    private void filtersReturnTrue(RequestFilter filter) {
        when(filter.isApplicable(any())).thenReturn(true);
    }

    private void filtersReturnFalse(RequestFilter filter) {
        when(filter.isApplicable(any())).thenReturn(false);
    }
}