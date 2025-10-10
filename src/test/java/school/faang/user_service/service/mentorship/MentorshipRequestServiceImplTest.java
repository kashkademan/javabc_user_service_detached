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
    private static final long FIXED_MENTEE_ID = 1L;
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
    }


    @Test
    void create_ErrorUserSentRequestToHimself() {
        CreateMentorshipRequestDto requestDto = new CreateMentorshipRequestDto("description1", 1L);
        when(userContext.getUserId()).thenReturn(FIXED_MENTEE_ID);

        assertThrows(DataValidationException.class,
                () -> service.create(requestDto));

        verify(mentorshipRequestRepository, never()).create(anyLong(), anyLong(), anyString());
    }

    Stream<Arguments> validArgs() {
        return Stream.of(
                Arguments.of(
                        FIXED_CREATE_MENTORSHIP_REQUEST_DTO,
                        LocalDateTime.now(),
                        RequestStatus.ACCEPTED,
                        "MentorCannotAcceptRequestFromUserIfHeIsAlreadyTheirMentor"),
                Arguments.of(
                        FIXED_CREATE_MENTORSHIP_REQUEST_DTO,
                        LocalDateTime.now(),
                        RequestStatus.PENDING,
                        "RequestForMentoringCanOnlyBeMadeOncePer"
                                + MENTORING_REQUEST_LIMITATION.toTotalMonths() + " months"),
                Arguments.of(
                        FIXED_CREATE_MENTORSHIP_REQUEST_DTO,
                        LocalDateTime.now().plus(MENTORING_REQUEST_LIMITATION),
                        RequestStatus.PENDING,
                        "MentoringRequestAlreadyExistsAndIsInTheStatus"
                                + RequestStatus.PENDING)
        );
    }

    @ParameterizedTest(name = " create_Error. {3}")
    @MethodSource("validArgs")
    void create_Error(CreateMentorshipRequestDto requestDto,
                      LocalDateTime createdAt,
                      RequestStatus status,
                      String description) {

        Optional<MentorshipRequest> mentorshipRequest = Optional.of(createMentorshipRequest(
                RequestStatus.ACCEPTED,
                FIXED_MENTEE_ID,
                requestDto.mentorId(),
                LocalDateTime.now().minus(MENTORING_REQUEST_LIMITATION)
        ));

        when(userContext.getUserId()).thenReturn(FIXED_MENTEE_ID);
        when(mentorshipRequestRepository
                .findLatestRequest(userContext.getUserId(), requestDto.mentorId()))
                .thenReturn(mentorshipRequest);

        assertThrows(DataValidationException.class,
                () -> service.create(requestDto));
    }

    @Test
    void create_CreateMentorshipRequest() {

        Optional<MentorshipRequest> mentorshipRequest = Optional.empty();


        record CreateArgs(Long menteeId, Long mentorId, String desc) {
        }

        CreateArgs args = new CreateArgs(
                FIXED_MENTEE_ID,
                FIXED_CREATE_MENTORSHIP_REQUEST_DTO.mentorId(),
                FIXED_CREATE_MENTORSHIP_REQUEST_DTO.description());


        when(userContext.getUserId()).thenReturn(FIXED_MENTEE_ID);

        when(mentorshipRequestRepository.findLatestRequest(userContext.getUserId(),
                FIXED_CREATE_MENTORSHIP_REQUEST_DTO.mentorId())).thenReturn(mentorshipRequest);

        MentorshipRequest createMentorshipRequest = createMentorshipRequest(
                RequestStatus.ACCEPTED,
                FIXED_MENTEE_ID,
                FIXED_CREATE_MENTORSHIP_REQUEST_DTO.mentorId(),
                LocalDateTime.now());

        when(mentorshipRequestRepository.create(
                args.menteeId(),
                args.mentorId(),
                args.desc())).thenReturn(createMentorshipRequest);

        MentorshipRequestDto resultCreateMentorshipRequestDto = service.create(FIXED_CREATE_MENTORSHIP_REQUEST_DTO);

        verify(mentorshipRequestRepository).create(
                args.menteeId(),
                args.mentorId(),
                args.desc()
        );
        MentorshipRequestDto createMentorshipRequestDto = mentorshipRequestMapper
                .toMentorshipRequestDto(createMentorshipRequest);

        assertEquals(createMentorshipRequestDto, resultCreateMentorshipRequestDto);
    }

    Stream<Arguments> validArgsGetByFilters() {

        return Stream.of(
                Arguments.of(
                        mentorshipRequestAll,
                        List.of(mentReqA12, mentReqA67, mentReqA87, mentReqA97),
                        new MentorshipRequestFilterDto(null, null, RequestStatus.ACCEPTED),
                        "EveryoneWithAcceptedStatus"),
                Arguments.of(
                        mentorshipRequestAll,
                        List.of(mentReqA12, mentReqR13, mentReqR14),
                        new MentorshipRequestFilterDto(FIXED_MENTEE_ID, null, null),
                        "AllRequestsCreatedByMentee"),
                Arguments.of(
                        mentorshipRequestAll,
                        List.of(mentReqA67, mentReqA87, mentReqA97),
                        new MentorshipRequestFilterDto(null, MENTOR_ID_7, null),
                        "AllRequestsCreatedByMentor"),
                Arguments.of(
                        mentorshipRequestAll,
                        mentorshipRequestAll,
                        new MentorshipRequestFilterDto(null, null, null),
                        "AnEmptyFilterReturnsAllRequests"),
                Arguments.of(
                        mentorshipRequestAll,
                        List.of(),
                        new MentorshipRequestFilterDto(MENTEE_ID_9, MENTOR_ID_9, null),
                        "NoRequestsFound"),
                Arguments.of(
                        mentorshipRequestAll,
                        List.of(),
                        new MentorshipRequestFilterDto(null, -MENTOR_ID_9, null),
                        "InvalidMentorIdFilterParameterNoRequests"),
                Arguments.of(
                        mentorshipRequestAll,
                        List.of(),
                        new MentorshipRequestFilterDto(-FIXED_MENTEE_ID, null, null),
                        "InvalidMenteeIdFilterParameterNoRequests"),
                Arguments.of(
                        mentorshipRequestAll,
                        List.of(mentReqA67),
                        new MentorshipRequestFilterDto(MENTEE_ID_6, MENTOR_ID_7, RequestStatus.ACCEPTED),
                        "FilterByAllThreeFieldsAtOnceMenteeIdMentorIdStatus)"),
                Arguments.of(
                        mentorshipRequestAll,
                        List.of(mentReqR13),
                        new MentorshipRequestFilterDto(null, MENTOR_ID_3, RequestStatus.REJECTED),
                        "AllMentorId3MentoringRefusals"),
                Arguments.of(
                        mentorshipRequestAll,
                        List.of(mentReqR23, mentReqP43),
                        new MentorshipRequestFilterDto(null, MENTOR_ID_3, RequestStatus.PENDING),
                        "All mentoring requests to MentorId3")
        );
    }

    @ParameterizedTest(name = "getByFilters_{3}")
    @MethodSource("validArgsGetByFilters")
    void getByFilters_Test(
            List<MentorshipRequest> mentReqAll,
            List<MentorshipRequest> filtrationResult,
            MentorshipRequestFilterDto filterDto,
            String testDescription
    ) {

        List<MentorshipRequestDto> expectedRequestsForMentoring = filtrationResult
                .stream()
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .toList();

        when(mentorshipRequestRepository.findAll()).thenReturn(mentReqAll);

        List<MentorshipRequestDto> filteredMentoringRequests = service.getByFilters(filterDto);

        verify(mentorshipRequestRepository).findAll();
        assertEquals(expectedRequestsForMentoring.size(), filteredMentoringRequests.size());
        assertEquals(new HashSet<>(expectedRequestsForMentoring), new HashSet<>(filteredMentoringRequests));
    }

    @Test
    void accept_ThereIsNoRequestForMentoringForMentee() {
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of());
        assertThrows(DataValidationException.class,
                () -> service.accept(FIXED_MENTEE_ID));
        verify(mentorshipRequestRepository, never()).findById(anyLong());
    }

    @Test
    void accept_MentorshipRequestHasBeenAcceptedByMentor() {
        MentorshipRequest mentorshipRequestFiltered = createMentorshipRequest(
                mentReqP43.getStatus(),
                mentReqP43.getRequester().getId(),
                mentReqP43.getReceiver().getId(),
                mentReqP43.getCreatedAt());

        Optional<MentorshipRequest> mentorshipRequestFilteredOptional = Optional.of(mentorshipRequestFiltered);

        when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequestAll);
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(mentorshipRequestFilteredOptional);
        ArgumentCaptor<MentorshipRequest> captor = ArgumentCaptor.forClass(MentorshipRequest.class);

        service.accept(MENTEE_ID_4);

        verify(mentorshipRequestRepository).save(captor.capture());
        MentorshipRequest passed = captor.getValue();

        assertEquals(mentorshipRequestFiltered.getId(), passed.getId());
        assertEquals(mentorshipRequestFiltered.getRequester().getId(), passed.getRequester().getId());
        assertEquals(mentorshipRequestFiltered.getReceiver().getId(), passed.getReceiver().getId());
        assertEquals(RequestStatus.ACCEPTED, passed.getStatus());
    }


    @Test
    void reject_hereIsNoRequestForMentoringForMentee() {
        RejectionDto rejectionDto = new RejectionDto("Причина отказа");
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of());
        assertThrows(DataValidationException.class,
                () -> service.reject(FIXED_MENTEE_ID, rejectionDto));
        verify(mentorshipRequestRepository, never()).findById(anyLong());
    }

    @Test
    void reject_hereIsNoRequestForMentee() {
        //находим нужный запрос на менторство и делаем отказ
        RejectionDto rejectionDto = new RejectionDto("Причина отказа");

        MentorshipRequest mentorshipRequestFiltered = createMentorshipRequest(
                mentReqP43.getStatus(),
                mentReqP43.getRequester().getId(),
                mentReqP43.getReceiver().getId(),
                mentReqP43.getCreatedAt());

        Optional<MentorshipRequest> mentorshipRequestFilteredOptional = Optional.of(mentorshipRequestFiltered);

        when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequestAll);
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(mentorshipRequestFilteredOptional);
        ArgumentCaptor<MentorshipRequest> captor = ArgumentCaptor.forClass(MentorshipRequest.class);

        service.reject(MENTEE_ID_4, rejectionDto);

        verify(mentorshipRequestRepository).save(captor.capture());
        MentorshipRequest passed = captor.getValue();

        assertEquals(mentorshipRequestFiltered.getId(), passed.getId());
        assertEquals(mentorshipRequestFiltered.getRequester().getId(), passed.getRequester().getId());
        assertEquals(mentorshipRequestFiltered.getReceiver().getId(), passed.getReceiver().getId());
        assertEquals(RequestStatus.REJECTED, passed.getStatus());
        assertEquals(rejectionDto.reason(), passed.getRejectionReason());
    }

}
