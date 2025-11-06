package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.event.mentorship.MentorshipOfferedEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestReceiverIdFilter;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestRequesterIdFilter;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestStatusFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipOfferedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceImplTest {

    private final int requestPeriod = 3;
    private final CreateMentorshipRequestDto createMentorshipRequestDto = CreateMentorshipRequestDto.builder()
            .mentorId(1532L)
            .description("some desc")
            .build();
    private final MentorshipRequest latestMentorship = new MentorshipRequest();
    private final MentorshipRequest mentorshipRequestForCreate = new MentorshipRequest();
    private final MentorshipRequest mentorshipRequestForAccept = new MentorshipRequest();
    private final MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);
    private MentorshipRequestFilterDto filterDto = MentorshipRequestFilterDto.builder()
            .status(RequestStatus.ACCEPTED)
            .build();
    private final RejectionDto rejectionDto = RejectionDto.builder()
            .reason("any reason")
            .build();
    private User mentor;
    private User mentee;

    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private  MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;
    @Spy
    private UserContext userContext;

    private MentorshipRequestServiceImpl mentorshipRequestService;

    @BeforeEach
    void setup() {
        mentorshipRequestService = new MentorshipRequestServiceImpl(userRepository, mentorshipRequestRepository,
                mentorshipRequestMapper, userContext, mentorshipOfferedEventPublisher,
                List.of(new MentorshipRequestReceiverIdFilter(), new MentorshipRequestRequesterIdFilter(),
                        new MentorshipRequestStatusFilter()));

        ReflectionTestUtils.setField(mentorshipRequestService, "periodForRequest", requestPeriod);

        userContext.setUserId(2);

        mentorshipRequestForCreate.setId(14345L);
        mentorshipRequestForCreate.setDescription(createMentorshipRequestDto.description());
        mentorshipRequestForCreate.setRequester(User.builder().id(userContext.getUserId()).build());
        mentorshipRequestForCreate.setReceiver(User.builder().id(createMentorshipRequestDto.mentorId()).build());

        mentor = User.builder()
                .id(userContext.getUserId())
                .build();

        mentee = User.builder()
                .id(mentor.getId() + 345)
                .build();
        mentor.setMentees(new ArrayList<>(List.of(User.builder().id(mentee.getId() + 3).build())));

        mentorshipRequestForAccept.setId(34634L);
        mentorshipRequestForAccept.setRequester(mentee);
        mentorshipRequestForAccept.setReceiver(mentor);
        mentorshipRequestForAccept.setStatus(RequestStatus.PENDING);
    }

    @Test
    void testCreateThrowsForbiddenExceptionIfTryToSendRequestToSelf() {
        userContext.setUserId(createMentorshipRequestDto.mentorId());
        ForbiddenException forbiddenException = Assertions.assertThrows(ForbiddenException.class,
                () -> mentorshipRequestService.create(createMentorshipRequestDto));
        Assertions.assertEquals("Forbidden to send mentorship request to self",
                forbiddenException.getMessage());
    }

    @Test
    void testCreateThrowsForbiddenExceptionIfTryToSendRequestMoreThanMaxTime() {
        latestMentorship.setCreatedAt(LocalDateTime.now().minusMonths(requestPeriod - 1));

        when(mentorshipRequestRepository.findLatestRequest(userContext.getUserId(),
                createMentorshipRequestDto.mentorId())).thenReturn(Optional.of(latestMentorship));

        ForbiddenException forbiddenException = Assertions.assertThrows(ForbiddenException.class,
                () -> mentorshipRequestService.create(createMentorshipRequestDto));
        Assertions.assertEquals("Forbidden to send mentorship request more than one time per three months",
                forbiddenException.getMessage());
    }

    @Test
    void testCreatePositive() {
        latestMentorship.setCreatedAt(LocalDateTime.now().minusMonths(requestPeriod + 1));

        when(mentorshipRequestRepository.findLatestRequest(userContext.getUserId(),
                createMentorshipRequestDto.mentorId())).thenReturn(Optional.of(latestMentorship));
        when(mentorshipRequestRepository.create(userContext.getUserId(), createMentorshipRequestDto.mentorId(),
                createMentorshipRequestDto.description())).thenReturn(mentorshipRequestForCreate);

        MentorshipRequestDto expectedMentorshipRequest
                = mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequestForCreate);
        MentorshipRequestDto mentorshipRequestDto = mentorshipRequestService.create(createMentorshipRequestDto);

        verify(mentorshipOfferedEventPublisher).publish(Mockito.any(MentorshipOfferedEvent.class));

        Assertions.assertEquals(expectedMentorshipRequest, mentorshipRequestDto);
    }

    @Test
    void testGetByFiltersThrowsExceptionIfNecessaryArgumentsAreNull() {
        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.getByFilters(filterDto));
        Assertions.assertEquals("Receiver id and requester id cant be both null",
                dataValidationException.getMessage());
    }

    @Test
    void testGetByFiltersPositive() {
        filterDto = filterDto
                .withRequesterId(1L)
                .withReceiverId(2L);
        MentorshipRequest correctMentorshipRequest = getMentorshipRequestForFilters(filterDto.status(),
                filterDto.requesterId(), filterDto.receiverId());
        MentorshipRequest wrongStatusMentorshipRequest = getMentorshipRequestForFilters(RequestStatus.REJECTED,
                filterDto.requesterId(), filterDto.receiverId());
        MentorshipRequest wrongRequesterMentorshipRequest = getMentorshipRequestForFilters(RequestStatus.REJECTED,
                filterDto.requesterId() + 1, filterDto.receiverId());
        MentorshipRequest wrongReceiverMentorshipRequest = getMentorshipRequestForFilters(RequestStatus.REJECTED,
                filterDto.requesterId(), filterDto.receiverId() + 1);

        when(mentorshipRequestRepository.findAll()).thenReturn(new ArrayList<>(List.of(correctMentorshipRequest,
                wrongStatusMentorshipRequest, wrongRequesterMentorshipRequest, wrongReceiverMentorshipRequest)));

        List<MentorshipRequestDto> byFiltersRequests = mentorshipRequestService.getByFilters(filterDto);
        MentorshipRequestDto expectedMentorshipRequestDto
                = mentorshipRequestMapper.toMentorshipRequestDto(correctMentorshipRequest);

        Assertions.assertEquals(1, byFiltersRequests.size());
        Assertions.assertEquals(expectedMentorshipRequestDto, byFiltersRequests.get(0));
    }

    @Test
    void testAcceptThrowsEntityNotFoundExceptionIfMentorshipNotFound() {
        final long id = 1L;
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.accept(id));
        Assertions.assertEquals("Membership request %d not found".formatted(id), entityNotFoundException.getMessage());
    }

    @Test
    void testAcceptThrowsEntityNotFoundExceptionIfMentorNotFound() {
        when(mentorshipRequestRepository.findById(mentorshipRequestForAccept.getId()))
                .thenReturn(Optional.of(mentorshipRequestForAccept));
        when(userRepository.getByIdOrThrow(userContext.getUserId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.accept(mentorshipRequestForAccept.getId()));
    }

    @Test
    void testAcceptThrowsForbiddenExceptionIfNotCurrentMentorMentorship() {
        mentor.setId(userContext.getUserId() + 1);

        acceptCustomMocks();

        ForbiddenException forbiddenException = Assertions.assertThrows(ForbiddenException.class,
                () -> mentorshipRequestService.accept(mentorshipRequestForAccept.getId()));
        Assertions.assertEquals("Forbidden to accept not own mentorship request",
                forbiddenException.getMessage());
    }

    @Test
    void testAcceptThrowsExceptionIfMentorContainsCurrentMentee() {
        mentor.setMentees(new ArrayList<>(List.of(mentee)));

        acceptCustomMocks();

        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.accept(mentorshipRequestForAccept.getId()));
        Assertions.assertEquals("Cant accept mentorship request. User %d is already mentor of user %d"
                .formatted(mentor.getId(), mentee.getId()), dataValidationException.getMessage());
    }

    @Test
    void testAcceptThrowsExceptionIfStatusIsWrong() {
        List<RequestStatus> requestStatuses = Arrays.stream(RequestStatus.values())
                .filter(status -> !status.equals(RequestStatus.PENDING)).toList();

        mentorshipRequestForAccept.setStatus(requestStatuses.get(new Random().nextInt(requestStatuses.size())));

        acceptCustomMocks();

        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.accept(mentorshipRequestForAccept.getId()));
        Assertions.assertEquals("Wrong status to accept. Must be 'Pending'. Current status is %s"
                .formatted(mentorshipRequestForAccept.getStatus()), dataValidationException.getMessage());
    }

    @Test
    void testAcceptPositive() {
        acceptCustomMocks();

        ArgumentCaptor<MentorshipRequest> requestCaptor = ArgumentCaptor.forClass(MentorshipRequest.class);
        mentorshipRequestService.accept(mentorshipRequestForAccept.getId());

        verify(mentorshipRequestRepository).save(requestCaptor.capture());
        MentorshipRequest actualMentorshipRequest = requestCaptor.getValue();

        Assertions.assertEquals(RequestStatus.ACCEPTED, actualMentorshipRequest.getStatus());
        Assertions.assertEquals(mentorshipRequestForAccept.getId(), actualMentorshipRequest.getId());
    }

    @Test
    void testRejectThrowsEntityNotFoundExceptionIfMentorshipNotFound() {
        final long id = 1L;
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.reject(id, rejectionDto));
        Assertions.assertEquals("Membership request %d not found".formatted(id), entityNotFoundException.getMessage());
    }

    @Test
    void testRejectThrowsForbiddenExceptionIfNotCurrentMentorMentorship() {
        userContext.setUserId(mentor.getId() + 1);

        rejectCustomMocks();

        ForbiddenException forbiddenException = Assertions.assertThrows(ForbiddenException.class,
                () -> mentorshipRequestService.reject(mentorshipRequestForAccept.getId(), rejectionDto));
        Assertions.assertEquals("Forbidden to reject not own mentorship request",
                forbiddenException.getMessage());
    }

    @Test
    void testRejectThrowsExceptionIfStatusIsWrong() {
        List<RequestStatus> requestStatuses = Arrays.stream(RequestStatus.values())
                .filter(status -> !status.equals(RequestStatus.PENDING)).toList();

        mentorshipRequestForAccept.setStatus(requestStatuses.get(new Random().nextInt(requestStatuses.size())));

        rejectCustomMocks();

        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.reject(mentorshipRequestForAccept.getId(), rejectionDto));
        Assertions.assertEquals("Wrong status to reject. Must be 'Pending'. Current status is %s"
                .formatted(mentorshipRequestForAccept.getStatus()), dataValidationException.getMessage());
    }

    @Test
    void testRejectPositive() {
        rejectCustomMocks();

        ArgumentCaptor<MentorshipRequest> requestCaptor = ArgumentCaptor.forClass(MentorshipRequest.class);
        mentorshipRequestService.reject(mentorshipRequestForAccept.getId(), rejectionDto);

        verify(mentorshipRequestRepository).save(requestCaptor.capture());
        MentorshipRequest actualMentorshipRequest = requestCaptor.getValue();

        Assertions.assertEquals(RequestStatus.REJECTED, actualMentorshipRequest.getStatus());
        Assertions.assertEquals(mentorshipRequestForAccept.getId(), actualMentorshipRequest.getId());
    }

    private MentorshipRequest getMentorshipRequestForFilters(RequestStatus status, Long requesterId, Long receiverId) {
        MentorshipRequest request = new MentorshipRequest();
        request.setStatus(status);
        request.setRequester(User.builder().id(requesterId).build());
        request.setReceiver(User.builder().id(receiverId).build());
        return request;
    }

    private void acceptCustomMocks() {
        when(mentorshipRequestRepository.findById(mentorshipRequestForAccept.getId()))
                .thenReturn(Optional.of(mentorshipRequestForAccept));
        when(userRepository.getByIdOrThrow(userContext.getUserId())).thenReturn(mentor);
    }

    private void rejectCustomMocks() {
        when(mentorshipRequestRepository.findById(mentorshipRequestForAccept.getId()))
                .thenReturn(Optional.of(mentorshipRequestForAccept));
    }
}