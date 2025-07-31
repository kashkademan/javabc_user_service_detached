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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
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

    private final Long requestId = 1L;
    private final User userRequester = User.builder().id(1L).build();
    private final User userReceiver = User.builder().id(2L).build();
    private final UserDto dtoUserRequester = UserDto.builder().id(1L).build();
    private final UserDto dtoUserReceiver = UserDto.builder().id(2L).build();
    private final MentorshipRequest mentorshipRequest = new MentorshipRequest(
            1L, "request to mentorship", userRequester, userReceiver, RequestStatus.ACCEPTED,
            null, LocalDateTime.now().minusMonths(4), LocalDateTime.now().minusMonths(4)
    );
    private final RejectionDto rejectionDto = new RejectionDto("declined");

    @BeforeEach
    void setUp() {
        mentorshipRequestService = new MentorshipRequestServiceImpl(
                mentorshipProperties,
                mentorshipRequestRepository,
                userRepository, mentorshipRequestMapper,
                userContext,
                mentorshipRepository
        );
        when(userContext.getUserId()).thenReturn(userRequester.getId());
    }

    @Test
    void positive_whenCreate_request_successfully() {
        CreateMentorshipRequestDto createRequest = new CreateMentorshipRequestDto(
                "desc", userReceiver.getId()
        );
        User user = mock(User.class);
        MentorshipRequest mentorshipRequest = new MentorshipRequest(
                1L, "desc", userRequester, userReceiver, RequestStatus.PENDING, " ",
                LocalDateTime.now().minusMonths(4), LocalDateTime.now());
        MentorshipRequestDto expectedDto = new MentorshipRequestDto(1L, "desc", dtoUserRequester,
                dtoUserReceiver, RequestStatus.PENDING, LocalDateTime.now().minusMonths(4), LocalDateTime.now());

        when(userRepository.findById(userRequester.getId())).thenReturn(Optional.of(user));
        when(mentorshipRequestRepository.findLatestRequest(
                userRequester.getId(), userReceiver.getId())).thenReturn(Optional.of(mentorshipRequest)
        );
        when(mentorshipRequestRepository.create(
                userRequester.getId(), userReceiver.getId(), createRequest.description())).thenReturn(mentorshipRequest
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
                1L, "Java mentoring", userRequester, userReceiver, RequestStatus.ACCEPTED,
                null, LocalDateTime.now(), LocalDateTime.now());

        List<MentorshipRequest> entryListOfRequests = List.of(request1);

        MentorshipRequestDto requestDto1 = new MentorshipRequestDto(
                1L, "Java mentoring", dtoUserRequester, dtoUserReceiver, RequestStatus.ACCEPTED,
                LocalDateTime.now(), LocalDateTime.now());

        when(mentorshipRequestRepository.findMentorshipRequestsByFilters(
                mentorshipRequestFilterDto.requesterId(), mentorshipRequestFilterDto.receiverId(),
                mentorshipRequestFilterDto.status())).thenReturn(entryListOfRequests
        );

        List<MentorshipRequestDto> resultDto = mentorshipRequestService.getByFilters(mentorshipRequestFilterDto);

        assertEquals(List.of(requestDto1), resultDto);
    }

    @Test
    void positive_when_apply_request_successfully() {
        when(userContext.getUserId()).thenReturn(userReceiver.getId());
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRepository.findMentorshipsByMentorAndMenteeIds(
                userReceiver.getId(), userRequester.getId())).thenReturn(List.of()
        );

        mentorshipRequestService.accept(requestId);

        Mockito.verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
        Mockito.verify(mentorshipRepository, times(
                1)).create(userReceiver.getId(), userRequester.getId()
        );
    }

    @Test
    void positive_when_reject_request_successfully() {
        when(userContext.getUserId()).thenReturn(userReceiver.getId());
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.reject(requestId, rejectionDto);

        Mockito.verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    void negative_when_create_request_aborted_by_date() {
        CreateMentorshipRequestDto dto = new CreateMentorshipRequestDto("desc", userReceiver.getId());
        User user = mock(User.class);
        MentorshipRequest mentorshipRequest = new MentorshipRequest(
                1L, "desc", userRequester, userReceiver, RequestStatus.PENDING, " ",
                LocalDateTime.now().minusMonths(1), LocalDateTime.now());

        when(userRepository.findById(userRequester.getId())).thenReturn(Optional.of(user));
        when(mentorshipRequestRepository.findLatestRequest(
                userRequester.getId(), userReceiver.getId())).thenReturn(Optional.of(mentorshipRequest)
        );
        when(mentorshipRequestRepository.create(userRequester.getId(), userReceiver.getId(), dto.description()))
                .thenReturn(mentorshipRequest);

        assertThrows(DataValidationException.class, () -> mentorshipRequestService.create(dto));
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

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.getByFilters(mentorshipRequestFilterDto));
    }

    @Test
    void negative_when_accept_not_found() {
        assertThrows(DataValidationException.class, () -> mentorshipRequestService.accept(1L));
    }

    @Test
    void negative_when_reject_not_found() {
        assertThrows(DataValidationException.class, () -> mentorshipRequestService.reject(1L, rejectionDto));
    }

}