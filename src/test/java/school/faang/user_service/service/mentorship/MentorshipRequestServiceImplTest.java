package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.mentorship.MentorshipProperties;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.mentorshp.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.MentorshipRejectException;
import school.faang.user_service.exception.RejectMentorshipRequestByDateException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(MentorshipProperties.class)
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
public class MentorshipRequestServiceImplTest {

    @Autowired
    private MentorshipProperties mentorshipProperties;
    @Spy
    @InjectMocks
    private MentorshipRequestMapperImpl mentorshipRequestMapper;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, Month.AUGUST, 1, 12, 0);
    private static final LocalDateTime UPDATED_AT = CREATED_AT.plusMonths(5);
    private static final Long REQUEST_ID = 1L;
    private static final User USER_REQUESTER = User.builder().id(1L).build();
    private static final User USER_RECEIVER = User.builder().id(2L).build();
    private static final UserDto DTO_USER_REQUESTER = UserDto.builder().id(1L).build();
    private static final UserDto DTO_USER_RECEIVER = UserDto.builder().id(2L).build();
    private static final MentorshipRequest MENTORSHIP_REQUEST = new MentorshipRequest(
            1L, "request to mentorship", USER_REQUESTER, USER_RECEIVER, RequestStatus.ACCEPTED,
            null, CREATED_AT.minusMonths(4), UPDATED_AT.minusMonths(4)
    );
    private static final RejectionDto REJECTION_DTO = new RejectionDto("declined");

    @BeforeEach
    void setUp() {
        mentorshipRequestService = new MentorshipRequestServiceImpl(
                mentorshipProperties,
                mentorshipRequestRepository,
                userRepository, mentorshipRequestMapper,
                userContext,
                mentorshipRepository
        );
        when(userContext.getUserId()).thenReturn(USER_REQUESTER.getId());
    }

    @Test
    void positive_whenCreate_request_successfully() {
        CreateMentorshipRequestDto createRequest = new CreateMentorshipRequestDto(
                "desc", USER_RECEIVER.getId()
        );
        User user = mock(User.class);
        MentorshipRequest mentorshipRequest = new MentorshipRequest(
                1L, "desc", USER_REQUESTER, USER_RECEIVER, RequestStatus.PENDING, " ",
                CREATED_AT.minusMonths(4), UPDATED_AT);
        MentorshipRequestDto expectedDto = new MentorshipRequestDto(1L, "desc", DTO_USER_REQUESTER,
                DTO_USER_RECEIVER, RequestStatus.PENDING, CREATED_AT.minusMonths(4), UPDATED_AT);

        when(userRepository.findById(USER_REQUESTER.getId())).thenReturn(Optional.of(user));
        when(mentorshipRequestRepository.findLatestRequest(
                USER_REQUESTER.getId(), USER_RECEIVER.getId())).thenReturn(Optional.of(mentorshipRequest)
        );
        when(
                mentorshipRequestRepository.create(USER_REQUESTER.getId(), USER_RECEIVER.getId(),
                        createRequest.description())).thenReturn(mentorshipRequest
        );

        MentorshipRequestDto actualDto = mentorshipRequestService.create(createRequest);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void positive_when_apply_filters_successfully() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto(
                1L, 2L, RequestStatus.ACCEPTED
        );
        MentorshipRequest request1 = new MentorshipRequest(
                1L, "Java mentoring", USER_REQUESTER, USER_RECEIVER, RequestStatus.ACCEPTED,
                null, CREATED_AT, UPDATED_AT);

        List<MentorshipRequest> entryListOfRequests = List.of(request1);

        MentorshipRequestDto requestDto1 = new MentorshipRequestDto(
                1L, "Java mentoring", DTO_USER_REQUESTER, DTO_USER_RECEIVER, RequestStatus.ACCEPTED,
                CREATED_AT, UPDATED_AT);

        when(mentorshipRequestRepository.findMentorshipRequestsByFilters(
                mentorshipRequestFilterDto.requesterId(), mentorshipRequestFilterDto.receiverId(),
                mentorshipRequestFilterDto.status())).thenReturn(entryListOfRequests
        );

        List<MentorshipRequestDto> resultDto = mentorshipRequestService.getByFilters(mentorshipRequestFilterDto);

        assertEquals(List.of(requestDto1), resultDto);
    }

    @Test
    void positive_when_apply_request_successfully() {
        when(userContext.getUserId()).thenReturn(USER_RECEIVER.getId());
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(MENTORSHIP_REQUEST));
        when(mentorshipRepository.findMentorshipsByMentorAndMenteeIds(
                USER_RECEIVER.getId(), USER_REQUESTER.getId())).thenReturn(List.of()
        );

        mentorshipRequestService.accept(REQUEST_ID);

        Mockito.verify(mentorshipRequestRepository, times(1)).save(MENTORSHIP_REQUEST);
        Mockito.verify(mentorshipRepository, times(
                1)).create(USER_RECEIVER.getId(), USER_REQUESTER.getId()
        );
    }

    @Test
    void positive_when_reject_request_successfully() {
        when(userContext.getUserId()).thenReturn(USER_RECEIVER.getId());
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(MENTORSHIP_REQUEST));

        mentorshipRequestService.reject(REQUEST_ID, REJECTION_DTO);

        Mockito.verify(mentorshipRequestRepository, times(1)).save(MENTORSHIP_REQUEST);
    }

    @Test
    void negative_when_create_request_aborted_by_date() {
        CreateMentorshipRequestDto dto = new CreateMentorshipRequestDto("desc", USER_RECEIVER.getId());
        User user = mock(User.class);
        MentorshipRequest mentorshipRequest = new MentorshipRequest(
                1L, "desc", USER_REQUESTER, USER_RECEIVER, RequestStatus.PENDING, " ",
                CREATED_AT.minusMonths(1), UPDATED_AT);

        when(userRepository.findById(USER_REQUESTER.getId())).thenReturn(Optional.of(user));
        when(mentorshipRequestRepository.findLatestRequest(
                USER_REQUESTER.getId(), USER_RECEIVER.getId())).thenReturn(Optional.of(mentorshipRequest)
        );
        when(mentorshipRequestRepository.create(USER_REQUESTER.getId(), USER_RECEIVER.getId(), dto.description()))
                .thenReturn(mentorshipRequest);

        assertThrows(RejectMentorshipRequestByDateException.class, () -> mentorshipRequestService.create(dto));
    }

    @Test
    void negative_when_create_request_aborted_because_request_is_null() {
        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.create(null));
    }

    @Test
    void negative_when_create_request_aborted_because_user_is_null() {
        CreateMentorshipRequestDto requestDto = new CreateMentorshipRequestDto("desc", null);
        assertThrows(UserNotFoundException.class, () -> mentorshipRequestService.create(requestDto));
    }

    @Test
    void negative_when_filter_is_empty() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto(
                2L, 2L, RequestStatus.ACCEPTED);
        List<MentorshipRequest> entryListOfRequests = List.of();

        when(mentorshipRequestRepository.findMentorshipRequestsByFilters(
                mentorshipRequestFilterDto.requesterId(), mentorshipRequestFilterDto.receiverId(),
                mentorshipRequestFilterDto.status())).thenReturn(entryListOfRequests
        );

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.getByFilters(mentorshipRequestFilterDto));
    }

    @Test
    void negative_when_accept_not_found() {
        assertThrows(EntityNotFoundException.class, () -> mentorshipRequestService.accept(1L));
    }

    @Test
    void negative_when_reject_not_found() {
        assertThrows(MentorshipRejectException.class, () -> mentorshipRequestService.reject(1L, REJECTION_DTO));
    }

}