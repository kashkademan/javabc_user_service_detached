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
import school.faang.user_service.filter.mentorship_request.RequesterFilter;
import school.faang.user_service.filter.mentorship_request.StatusFilter;
import school.faang.user_service.mapper.RequestToResponseDtoImpl;
import school.faang.user_service.publisher.MentorshipRequestEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestEventPublisher mentorshipRequestEventPublisher;
    @Spy
    private RequestToResponseDtoImpl responseMapper;

    private DescriptionFilter descriptionFilter;
    private ReceiverFilter receiverFilter;
    private RequesterFilter requesterFilter;
    private StatusFilter statusFilter;

    @BeforeEach
    public void setup() {
        descriptionFilter = new DescriptionFilter();
        receiverFilter = new ReceiverFilter();
        requesterFilter = new RequesterFilter();
        statusFilter = new StatusFilter();

        mentorshipRequestService = new MentorshipRequestService(
                mentorshipRequestRepository,
                responseMapper,
                List.of(descriptionFilter, receiverFilter, requesterFilter, statusFilter),
                mentorshipRequestEventPublisher
        );
    }

    @Test
    void testRequestMentorshipExceptionEqualsIdDto() {
        long id = 1L;
        MentorshipRequestDto dto = createMentorshipDto("asd", id, id, RequestStatus.ACCEPTED,
                LocalDateTime.now(), LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    void testRequestMentorshipExceptionThreeMonths() {
        long requesterId = 1L;
        long receiverId = 2L;
        MentorshipRequestDto dto = createMentorshipDto("asd", requesterId, receiverId,
                RequestStatus.ACCEPTED, LocalDateTime.now(), LocalDateTime.now());

        User requester = createUserById(requesterId);
        User receiver = createUserById(receiverId);

        Optional<MentorshipRequest> optionalNewMentorShip =
                Optional.of(new MentorshipRequest(3L, "qwe", requester, receiver, RequestStatus.ACCEPTED,
                        "",
                        LocalDateTime.of(2025, 1, 1, 1, 1),
                        LocalDateTime.of(2025, 1, 1, 1, 1)));

        when(mentorshipRequestRepository
                .findLatestRequest(dto.requesterId(), dto.receiverId())).thenReturn(optionalNewMentorShip);

        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    void testRequestMentorship() {
        long requesterId = 1L;
        long receiverId = 2L;
        MentorshipRequestDto dto = createMentorshipDto("asd", requesterId, receiverId, RequestStatus.ACCEPTED,
                LocalDateTime.now(), LocalDateTime.now());

        User requester = createUserById(requesterId);
        User receiver = createUserById(receiverId);

        Optional<MentorshipRequest> optionalMentorshipRequest =
                Optional.of(new MentorshipRequest(3L, "qwe", requester, receiver, RequestStatus.ACCEPTED,
                        "",
                        LocalDateTime.of(2025, 1, 1, 1, 1),
                        LocalDateTime.of(2025, 5, 1, 1, 1)));

        MentorshipRequest mentorshipRequest =
                new MentorshipRequest(1L, "asdf", requester, receiver, RequestStatus.ACCEPTED, "",
                        LocalDateTime.of(2025, 1, 1, 1, 1),
                        LocalDateTime.of(2025, 5, 1, 1, 1));

        when(mentorshipRequestRepository
                .findLatestRequest(dto.requesterId(), dto.receiverId())).thenReturn(optionalMentorshipRequest);
        when(mentorshipRequestRepository.create(dto.requesterId(), dto.receiverId(), dto.description()))
                .thenReturn(mentorshipRequest);

        MentorshipResponseDto result = mentorshipRequestService.requestMentorship(dto);

        MentorshipResponseDto responseDto = new MentorshipResponseDto(1L, "asdf", requesterId, receiverId,
                RequestStatus.ACCEPTED,
                LocalDateTime.of(2025, 1, 1, 1, 1),
                LocalDateTime.of(2025, 5, 1, 1, 1));

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
        MentorshipRequest request1 = createMentorshipRequest(1L, "", 1L, 2L,
                RequestStatus.ACCEPTED);

        MentorshipRequest request2 = createMentorshipRequest(1L, "", 3L, 4L,
                RequestStatus.ACCEPTED);

        MentorshipRequest request3 = createMentorshipRequest(1L, "", 2L, 3L,
                RequestStatus.ACCEPTED);

        MentorshipRequestFilterDto dto = new MentorshipRequestFilterDto(null, 1L, null, null);
        List<MentorshipRequest> list = List.of(request1, request2, request3);

        when(mentorshipRequestRepository.findAll()).thenReturn(list);

        List<MentorshipResponseDto> result = mentorshipRequestService.getRequests(dto);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).receiverId());
    }

    @Test
    void testGetRequestsTwoFilter() {
        MentorshipRequest request1 = createMentorshipRequest(1L, "test one", 1L, 2L,
                RequestStatus.ACCEPTED);

        MentorshipRequest request2 = createMentorshipRequest(1L, "test two", 3L, 4L,
                RequestStatus.ACCEPTED);

        MentorshipRequest request3 = createMentorshipRequest(1L, "test three", 2L, 3L,
                RequestStatus.ACCEPTED);

        MentorshipRequestFilterDto dto = new MentorshipRequestFilterDto("test", 1L, null, null);
        List<MentorshipRequest> list = List.of(request1, request2, request3);

        when(mentorshipRequestRepository.findAll()).thenReturn(list);

        List<MentorshipResponseDto> result = mentorshipRequestService.getRequests(dto);

        assertEquals(1, result.size());
        assertEquals("test one", result.get(0).description());
    }

    @Test
    void testGetRequestsThreeFilter() {
        MentorshipRequest request1 = createMentorshipRequest(1L, "test one", 1L, 2L,
                RequestStatus.ACCEPTED);

        MentorshipRequest request2 = createMentorshipRequest(1L, "test two", 3L, 4L,
                RequestStatus.ACCEPTED);

        MentorshipRequest request3 = createMentorshipRequest(1L, "test three", 2L, 5L,
                RequestStatus.PENDING);

        MentorshipRequestFilterDto dto = new MentorshipRequestFilterDto("test", 1L, null, RequestStatus.PENDING);
        List<MentorshipRequest> list = List.of(request1, request2, request3);

        when(mentorshipRequestRepository.findAll()).thenReturn(list);

        List<MentorshipResponseDto> result = mentorshipRequestService.getRequests(dto);

        assertEquals(0, result.size());
    }

    @Test
    void testGetRequestsFourFilter() {
        MentorshipRequest request1 = createMentorshipRequest(1L, "test one", 1L, 2L,
                RequestStatus.ACCEPTED);

        MentorshipRequest request2 = createMentorshipRequest(2L, "test two", 3L, 4L,
                RequestStatus.ACCEPTED);

        MentorshipRequest request3 = createMentorshipRequest(3L, "test three", 2L, 5L,
                RequestStatus.ACCEPTED);

        MentorshipRequestFilterDto dto = new MentorshipRequestFilterDto("test", 1L, 2L, RequestStatus.ACCEPTED);
        List<MentorshipRequest> list = List.of(request1, request2, request3);

        when(mentorshipRequestRepository.findAll()).thenReturn(list);

        List<MentorshipResponseDto> result = mentorshipRequestService.getRequests(dto);

        assertEquals(1, result.size());
    }

    private MentorshipRequest createMentorshipRequest(Long requestId, String description, Long userid1, Long userId2,
                                                      RequestStatus status) {
        return new MentorshipRequest(requestId,
                description,
                User.builder().id(userid1).build(),
                User.builder().id(userId2).build(),
                status,
                "",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private MentorshipRequestDto createMentorshipDto(String description, long requesterId, long receiverId,
                                                     RequestStatus status, LocalDateTime createdAt,
                                                     LocalDateTime updatedAt) {
        return new MentorshipRequestDto(description, requesterId, receiverId,
                status, createdAt, updatedAt);
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
}