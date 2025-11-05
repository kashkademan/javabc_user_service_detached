package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filters.mentorship.MentorshipRequestFilter;
import school.faang.user_service.filters.mentorship.MentorshipRequestFilterMentee;
import school.faang.user_service.filters.mentorship.MentorshipRequestFilterMentor;
import school.faang.user_service.filters.mentorship.MentorshipRequestFilterStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MentorshipRequestServiceImplTest extends DataForTests {

    private static final Period MENTORING_REQUEST_LIMITATION = Period.ofMonths(3);
    private static final RejectionDto FIXED_REJECTION_DTO = new RejectionDto("The reason for refusal is indicated.");
    private static final CreateMentorshipRequestDto FIXED_CREATE_MENTORSHIP_REQUEST_DTO =
            new CreateMentorshipRequestDto("description1", 2L);

    @Spy
    private MentorshipRequestFilterMentee mentorshipRequestFilterMentee;
    @Spy
    private MentorshipRequestFilterStatus mentorshipRequestFilterStatus;
    @Spy
    private MentorshipRequestFilterMentor mentorshipRequestFilterMentor;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);
    @Mock
    private UserContext userContext;
    private MentorshipRequestServiceImpl service;
    private MentorshipRequest mentorshipRequestFiltered;
    private MentorshipRequest filteredRequestWithInvalidStatus;

    @BeforeEach
    void configureTest() {
        List<MentorshipRequestFilter> mentorshipRequestFilters = List.of(
                mentorshipRequestFilterMentee,
                mentorshipRequestFilterMentor,
                mentorshipRequestFilterStatus);

        service = new MentorshipRequestServiceImpl(
                mentorshipRequestRepository,
                mentorshipRequestMapper,
                mentorshipRequestFilters,
                userContext,
                MENTORING_REQUEST_LIMITATION
        );

        mentorshipRequestFiltered = createMentorshipRequest(
                mentReqP43.getStatus(),
                mentReqP43.getRequester().getId(),
                mentReqP43.getReceiver().getId(),
                mentReqP43.getCreatedAt());

        filteredRequestWithInvalidStatus = createMentorshipRequest(
                mentReqA12.getStatus(),
                mentReqA12.getRequester().getId(),
                mentReqA12.getReceiver().getId(),
                mentReqA12.getCreatedAt());
    }

    @Test
    void create_ErrorUserSentRequestToHimself() {
        CreateMentorshipRequestDto requestDto = new CreateMentorshipRequestDto("description1", 1L);
        when(userContext.getUserId()).thenReturn(MENTEE_ID_1);

        assertThrows(DataValidationException.class,
                () -> service.create(requestDto));

        verify(mentorshipRequestRepository, never()).create(anyLong(), anyLong(), anyString());
    }

    Stream<Arguments> invalidCreateArgs() {
        return Stream.of(
                Arguments.of(
                        FIXED_CREATE_MENTORSHIP_REQUEST_DTO,
                        FIXED_LOCAL_DATE_TIME,
                        RequestStatus.ACCEPTED,
                        "MentorCannotAcceptRequestFromUserIfHeIsAlreadyTheirMentor"),
                Arguments.of(
                        FIXED_CREATE_MENTORSHIP_REQUEST_DTO,
                        FIXED_LOCAL_DATE_TIME,
                        RequestStatus.PENDING,
                        "RequestForMentoringCanOnlyBeMadeOncePer"
                                + MENTORING_REQUEST_LIMITATION.toTotalMonths() + " months"),
                Arguments.of(
                        FIXED_CREATE_MENTORSHIP_REQUEST_DTO,
                        FIXED_LOCAL_DATE_TIME.plus(MENTORING_REQUEST_LIMITATION),
                        RequestStatus.PENDING,
                        "MentoringRequestAlreadyExistsAndIsInTheStatus"
                                + RequestStatus.PENDING)
        );
    }

    @ParameterizedTest(name = "create_Error. {3}")
    @MethodSource("invalidCreateArgs")
    void create_Error(CreateMentorshipRequestDto requestDto,
                      LocalDateTime createdAt,
                      RequestStatus status,
                      String description) {

        Optional<MentorshipRequest> mentorshipRequest = Optional.of(createMentorshipRequest(
                status,
                MENTEE_ID_1,
                requestDto.mentorId(),
                createdAt
        ));

        when(userContext.getUserId()).thenReturn(MENTEE_ID_1);
        when(mentorshipRequestRepository
                .findLatestRequest(userContext.getUserId(), requestDto.mentorId()))
                .thenReturn(mentorshipRequest);

        assertThrows(DataValidationException.class,
                () -> service.create(requestDto));
    }

    @Test
    void create_CreateMentorshipRequest() {
        Optional<MentorshipRequest> mentorshipRequest = Optional.empty();

        when(userContext.getUserId()).thenReturn(MENTEE_ID_1);
        when(mentorshipRequestRepository.findLatestRequest(userContext.getUserId(),
                FIXED_CREATE_MENTORSHIP_REQUEST_DTO.mentorId())).thenReturn(mentorshipRequest);

        MentorshipRequest createMentorshipRequest = createMentorshipRequest(
                RequestStatus.PENDING,
                MENTEE_ID_1,
                FIXED_CREATE_MENTORSHIP_REQUEST_DTO.mentorId(),
                FIXED_LOCAL_DATE_TIME);

        when(mentorshipRequestRepository.create(
                MENTEE_ID_1,
                FIXED_CREATE_MENTORSHIP_REQUEST_DTO.mentorId(),
                FIXED_CREATE_MENTORSHIP_REQUEST_DTO.description())).thenReturn(createMentorshipRequest);

        MentorshipRequestDto resultCreateMentorshipRequestDto = service.create(FIXED_CREATE_MENTORSHIP_REQUEST_DTO);

        verify(mentorshipRequestRepository).create(
                MENTEE_ID_1,
                FIXED_CREATE_MENTORSHIP_REQUEST_DTO.mentorId(),
                FIXED_CREATE_MENTORSHIP_REQUEST_DTO.description()
        );
        MentorshipRequestDto createMentorshipRequestDto = mentorshipRequestMapper
                .toMentorshipRequestDto(createMentorshipRequest);
        assertEquals(createMentorshipRequestDto, resultCreateMentorshipRequestDto);
        assertEquals(createMentorshipRequestDto.status(), RequestStatus.PENDING);
    }

    Stream<Arguments> validArgsGetByFilters() {
        return Stream.of(
                Arguments.of(
                        List.of(mentReqA12, mentReqA67, mentReqA87, mentReqA97),
                        new MentorshipRequestFilterDto(null, null, RequestStatus.ACCEPTED),
                        "EveryoneWithAcceptedStatus"),
                Arguments.of(
                        List.of(mentReqA12, mentReqR13, mentReqR14),
                        new MentorshipRequestFilterDto(MENTEE_ID_1, null, null),
                        "AllRequestsCreatedByMentee"),
                Arguments.of(
                        List.of(mentReqA67, mentReqA87, mentReqA97),
                        new MentorshipRequestFilterDto(null, MENTOR_ID_7, null),
                        "AllRequestsCreatedByMentor"),
                Arguments.of(
                        mentorshipRequestAll,
                        new MentorshipRequestFilterDto(null, null, null),
                        "AnEmptyFilterReturnsAllRequests"),
                Arguments.of(
                        List.of(),
                        new MentorshipRequestFilterDto(MENTEE_ID_9, MENTOR_ID_9, null),
                        "NoRequestsFound"),
                Arguments.of(
                        List.of(),
                        new MentorshipRequestFilterDto(null, -MENTOR_ID_9, null),
                        "InvalidMentorIdFilterParameterNoRequests"),
                Arguments.of(
                        List.of(),
                        new MentorshipRequestFilterDto(-MENTEE_ID_1, null, null),
                        "InvalidMenteeIdFilterParameterNoRequests"),
                Arguments.of(
                        List.of(mentReqA67),
                        new MentorshipRequestFilterDto(MENTEE_ID_6, MENTOR_ID_7, RequestStatus.ACCEPTED),
                        "FilterByAllThreeFieldsAtOnceMenteeIdMentorIdStatus)"),
                Arguments.of(
                        List.of(mentReqR13),
                        new MentorshipRequestFilterDto(null, MENTOR_ID_3, RequestStatus.REJECTED),
                        "AllMentorId3MentoringRefusals"),
                Arguments.of(
                        List.of(mentReqP23, mentReqP43),
                        new MentorshipRequestFilterDto(null, MENTOR_ID_3, RequestStatus.PENDING),
                        "All mentoring requests to MentorId3")
        );
    }

    @ParameterizedTest(name = "getByFilters_{2}")
    @MethodSource("validArgsGetByFilters")
    void getByFilters_Test(
            List<MentorshipRequest> filtrationResult,
            MentorshipRequestFilterDto filterDto,
            String testDescription
    ) {

        List<MentorshipRequestDto> expectedRequestsForMentoring = filtrationResult
                .stream()
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .toList();

        when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequestAll);
        List<MentorshipRequestDto> filteredMentoringRequests = service.getByFilters(filterDto);
        verify(mentorshipRequestRepository).findAll();
        assertEquals(expectedRequestsForMentoring.size(), filteredMentoringRequests.size());
        assertEquals(new HashSet<>(expectedRequestsForMentoring), new HashSet<>(filteredMentoringRequests));
    }

    @Test
    void accept_MentorshipRequestNotFound() {
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.accept(FIXED_MENTORSHIP_REQUEST_ID));
    }

    @Test
    void accept_MentoringRequestHasStatusOtherThanPending() {
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.of(filteredRequestWithInvalidStatus));
        assertThrows(DataValidationException.class,
                () -> service.accept(FIXED_MENTORSHIP_REQUEST_ID));
    }

    @Test
    void accept_ChangesStatusMentorshipRequestAndSavesToDatabase() {
        Optional<MentorshipRequest> mentorshipRequestFilteredOptional = Optional.of(mentorshipRequestFiltered);
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(mentorshipRequestFilteredOptional);
        ArgumentCaptor<MentorshipRequest> captorSaveMentorshipRequest = ArgumentCaptor
                .forClass(MentorshipRequest.class);
        service.accept(FIXED_MENTORSHIP_REQUEST_ID);
        verify(mentorshipRequestRepository).save(captorSaveMentorshipRequest.capture());
        MentorshipRequest passed = captorSaveMentorshipRequest.getValue();
        assertEquals(mentorshipRequestFiltered.getId(), passed.getId());
        assertEquals(mentorshipRequestFiltered.getRequester().getId(), passed.getRequester().getId());
        assertEquals(mentorshipRequestFiltered.getReceiver().getId(), passed.getReceiver().getId());
        assertEquals(RequestStatus.ACCEPTED, passed.getStatus());
    }

    @Test
    void reject_MentorshipRequestNotFound() {

        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.reject(FIXED_MENTORSHIP_REQUEST_ID, FIXED_REJECTION_DTO));
    }

    @Test
    void reject_MentoringRequestHasStatusOtherThanPending() {
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.of(filteredRequestWithInvalidStatus));
        assertThrows(DataValidationException.class,
                () -> service.reject(FIXED_MENTORSHIP_REQUEST_ID, FIXED_REJECTION_DTO));
    }

    @Test
    void reject_MentoringRequestHasStatusOtherThanRejected() {
        Optional<MentorshipRequest> mentorshipRequestFilteredOptional = Optional.of(mentorshipRequestFiltered);
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(mentorshipRequestFilteredOptional);
        ArgumentCaptor<MentorshipRequest> captorSaveMentorshipRequest = ArgumentCaptor
                .forClass(MentorshipRequest.class);
        service.reject(FIXED_MENTORSHIP_REQUEST_ID, FIXED_REJECTION_DTO);

        verify(mentorshipRequestRepository).save(captorSaveMentorshipRequest.capture());
        MentorshipRequest passed = captorSaveMentorshipRequest.getValue();
        assertEquals(mentorshipRequestFiltered.getId(), passed.getId());
        assertEquals(mentorshipRequestFiltered.getRequester().getId(), passed.getRequester().getId());
        assertEquals(mentorshipRequestFiltered.getReceiver().getId(), passed.getReceiver().getId());
        assertEquals(RequestStatus.REJECTED, passed.getStatus());
        assertEquals(FIXED_REJECTION_DTO.reason(), passed.getRejectionReason());
    }
}
