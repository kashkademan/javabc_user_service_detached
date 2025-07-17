package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation_request.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.entity.RequestStatus.PENDING;
import static school.faang.user_service.entity.RequestStatus.REJECTED;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceImplTest {
    @Mock
    private UserContext userContext;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestFilter recommendationRequestMessageContainsFilter;

    @Mock
    private RecommendationRequestFilter recommendationRequestReceiverIdFilter;

    @Mock
    private RecommendationRequestFilter recommendationRequestRequesterIdFilter;

    @Mock
    private RecommendationRequestFilter recommendationRequestStatusFilter;

    private RecommendationRequestServiceImpl recommendationRequestService;

    @BeforeEach
    void setUp() {
        recommendationRequestService = new RecommendationRequestServiceImpl(
                recommendationRequestRepository,
                userRepository,
                recommendationRequestMapper,
                userContext,
                skillRequestRepository,
                Set.of(recommendationRequestMessageContainsFilter,
                        recommendationRequestReceiverIdFilter,
                        recommendationRequestRequesterIdFilter,
                        recommendationRequestStatusFilter)
        );
    }

    @AfterEach
    void tearDown() {
        recommendationRequestService = null;
    }

    @Test
    @DisplayName("Create recommendation request with requesterId equals receiverId")
    void testCreateRequesterIdEqualsReceiverId() {
        when(userContext.getUserId()).thenReturn(1L);
        long requesterId = userContext.getUserId();
        long receiverId = 1L;

        CreateRecommendationRequestDto createRecommendationRequestDto =
                new CreateRecommendationRequestDto(
                        receiverId,
                        null,
                        null);

        assertEquals(receiverId, requesterId);
        assertThrows(DataValidationException.class, () ->
                recommendationRequestService
                        .create(createRecommendationRequestDto));
    }

    @Test
    @DisplayName("Create recommendation request without latest pending request")
    void testCreateWithoutLatestPendingRequest() {
        when(userContext.getUserId()).thenReturn(2L);
        long requesterId = userContext.getUserId();
        long receiverId = 1L;

        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.empty());

        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository
                .findLatestPendingRequest(requesterId, receiverId);

        assertFalse(latestPendingRequest.isPresent());
    }

    @Test
    @DisplayName("Create recommendation request earlier than allowed period")
    void testCreateEarlierThanAllowedPeriod() {
        when(userContext.getUserId()).thenReturn(3L);
        long requesterId = userContext.getUserId();
        long receiverId = 2L;
        long quantity = 6L;
        TemporalUnit period = ChronoUnit.MONTHS;

        RecommendationRequest recommendationRequest =
                RecommendationRequest.builder()
                        .requester(User.builder().id(requesterId).build())
                        .receiver(User.builder().id(receiverId).build())
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build();

        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.of(recommendationRequest));

        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository
                .findLatestPendingRequest(requesterId, receiverId);

        CreateRecommendationRequestDto createRecommendationRequestDto =
                new CreateRecommendationRequestDto(requesterId, "some message", null);

        assertTrue(latestPendingRequest.isPresent());
        assertTrue(latestPendingRequest.get().getCreatedAt()
                .plus(quantity, period).isAfter(LocalDateTime.now()));
        assertThrows(DataValidationException.class, () ->
                recommendationRequestService.create(createRecommendationRequestDto));
    }

    @Test
    @DisplayName("Create recommendation request later than allowed period")
    void testCreateLaterThanAllowedPeriod() {
        when(userContext.getUserId()).thenReturn(4L);
        long requesterId = userContext.getUserId();
        long receiverId = 3L;
        long quantity = 6L;
        TemporalUnit period = ChronoUnit.MONTHS;

        RecommendationRequest recommendationRequest =
                RecommendationRequest.builder()
                        .requester(User.builder().id(requesterId).build())
                        .receiver(User.builder().id(receiverId).build())
                        .createdAt(LocalDateTime.now().minusMonths(quantity).minusDays(1))
                        .build();

        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.of(recommendationRequest));

        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository
                .findLatestPendingRequest(requesterId, receiverId);

        assertTrue(latestPendingRequest.isPresent());
        assertFalse(latestPendingRequest.get().getCreatedAt()
                .plus(quantity, period).isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Create recommendation request with skillIds is null")
    void testCreateWithSkillIdsIsNull() {
        long receiverId = 4L;

        CreateRecommendationRequestDto recommendationDto =
                new CreateRecommendationRequestDto(
                        receiverId,
                        "some message",
                        null);

        assertNull(recommendationDto.skillIds());
    }

    @Test
    @DisplayName("Create recommendation request with skillIds is not null")
    void testCreateWithSkillIdsIsNotNull() {
        when(userContext.getUserId()).thenReturn(6L);
        long requesterId = userContext.getUserId();
        long receiverId = 5L;

        List<Long> skillIds = List.of(1L, 2L, 3L);
        CreateRecommendationRequestDto recommendationDto =
                new CreateRecommendationRequestDto(
                        receiverId,
                        "some message",
                        skillIds);

        RecommendationRequest recommendationRequestForSave =
                recommendationRequestMapper
                        .toRecommendationRequest(recommendationDto);

        verify(recommendationRequestMapper, times(1))
                .toRecommendationRequest(recommendationDto);

        when(userRepository.getByIdOrThrow(requesterId))
                .thenReturn(User.builder().id(requesterId).build());

        recommendationRequestForSave.setRequester(userRepository
                .getByIdOrThrow(requesterId));

        when(userRepository.getByIdOrThrow(receiverId))
                .thenReturn(User.builder().id(receiverId).build());

        recommendationRequestForSave.setReceiver(userRepository
                .getByIdOrThrow(receiverId));
        recommendationRequestForSave.setStatus(PENDING);

        RecommendationRequest recommendationRequest = recommendationRequestForSave;
        recommendationRequest.setId(111L);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());

        when(recommendationRequestRepository.save(recommendationRequestForSave))
                .thenReturn(recommendationRequest);

        recommendationRequestRepository.save(recommendationRequestForSave);

        when(skillRequestRepository.create(anyLong(), anyLong()))
                .thenReturn(new SkillRequest());

        for (long skillId : recommendationDto.skillIds()) {
            SkillRequest skillRequest = skillRequestRepository
                    .create(recommendationRequest.getId(), skillId);
            recommendationRequest.addSkillRequest(skillRequest);
        }

        assertNotNull(recommendationDto.skillIds());

    }

    @Test
    @DisplayName("Get all recommendation requests by filters")
    void testGetByFilters() {
        List<RecommendationRequest> recommendationRequests = List.of(
                RecommendationRequest.builder()
                        .requester(User.builder().id(1L).build())
                        .receiver(User.builder().id(2L).build())
                        .message("some message")
                        .status(PENDING)
                        .build(),
                RecommendationRequest.builder()
                        .requester(User.builder().id(2L).build())
                        .receiver(User.builder().id(3L).build())
                        .message("other message")
                        .status(ACCEPTED)
                        .build(),
                RecommendationRequest.builder()
                        .requester(User.builder().id(3L).build())
                        .receiver(User.builder().id(1L).build())
                        .message("another message")
                        .status(REJECTED)
                        .build()
        );

        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);

        when(recommendationRequestMessageContainsFilter.isApplicable(any())).thenReturn(true);
        when(recommendationRequestRequesterIdFilter.isApplicable(any())).thenReturn(true);
        when(recommendationRequestReceiverIdFilter.isApplicable(any())).thenReturn(true);
        when(recommendationRequestStatusFilter.isApplicable(any())).thenReturn(true);

        when(recommendationRequestMessageContainsFilter.apply(any(), any()))
                .thenAnswer(new Answer<Stream<RecommendationRequest>>() {
                    @Override
                    public Stream<RecommendationRequest> answer(InvocationOnMock invocation) throws Throwable {
                        Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                        return recommendationRequestStream.filter(recommendationRequest ->
                                recommendationRequest.getMessage().toLowerCase().contains("Message".toLowerCase()));
                    }
                });

        when(recommendationRequestRequesterIdFilter.apply(any(), any()))
                .thenAnswer(new Answer<Stream<RecommendationRequest>>() {
                    @Override
                    public Stream<RecommendationRequest> answer(InvocationOnMock invocation) throws Throwable {
                        Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                        return recommendationRequestStream.filter(recommendationRequest ->
                                recommendationRequest.getRequester().getId().equals(3L));
                    }
                });

        when(recommendationRequestReceiverIdFilter.apply(any(), any()))
                .thenAnswer(new Answer<Stream<RecommendationRequest>>() {
                    @Override
                    public Stream<RecommendationRequest> answer(InvocationOnMock invocation) throws Throwable {
                        Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                        return recommendationRequestStream.filter(recommendationRequest ->
                                recommendationRequest.getReceiver().getId().equals(1L));
                    }
                });

        when(recommendationRequestStatusFilter.apply(any(), any()))
                .thenAnswer(new Answer<Stream<RecommendationRequest>>() {
                    @Override
                    public Stream<RecommendationRequest> answer(InvocationOnMock invocation) throws Throwable {
                        Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                        return recommendationRequestStream.filter(recommendationRequest ->
                                recommendationRequest.getStatus().equals(REJECTED));
                    }
                });

        List<RecommendationRequestDto> result = recommendationRequestService
                .getByFilters(new RecommendationRequestFilterDto(
                        null,
                        null,
                        null,
                        null
                ));

        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("Get recommendation request by valid ID")
    void testGetById() {
        long requestId = 1L;
        RecommendationRequest request = RecommendationRequest.builder()
                .id(requestId)
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .message("Test message")
                .status(PENDING)
                .build();

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        RecommendationRequestDto result = recommendationRequestService.getById(requestId);

        assertEquals(request.getId(), result.id());
        assertEquals(request.getMessage(), result.message());
        assertEquals(request.getStatus(), result.status());
    }

    @Test
    @DisplayName("Get recommendation request by invalid ID")
    void testGetByIdNotFound() {
        long invalidId = 999L;
        when(recommendationRequestRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationRequestService.getById(invalidId));
    }

    @Test
    @DisplayName("Accept recommendation request successfully")
    void testAccept() {
        long requestId = 1L;
        long receiverId = 2L;

        RecommendationRequest request = RecommendationRequest.builder()
                .id(requestId)
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(receiverId).build())
                .status(PENDING)
                .build();

        when(userContext.getUserId()).thenReturn(receiverId);
        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        recommendationRequestService.accept(requestId);

        assertEquals(ACCEPTED, request.getStatus());
        verify(recommendationRequestRepository).save(request);
    }

    @Test
    @DisplayName("Reject recommendation request successfully")
    void testReject() {
        long requestId = 1L;
        long receiverId = 2L;

        RecommendationRequest request = RecommendationRequest.builder()
                .id(requestId)
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(receiverId).build())
                .status(PENDING)
                .build();

        when(userContext.getUserId()).thenReturn(receiverId);
        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        RejectionDto rejectionDto = new RejectionDto("Rejection reason");
        recommendationRequestService.reject(requestId, rejectionDto);

        assertEquals(REJECTED, request.getStatus());
        verify(recommendationRequestRepository).save(request);
    }
}