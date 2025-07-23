package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.recommendation_request.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
@ExtendWith(SpringExtension.class)
class RecommendationRequestServiceImplTest {
    @Mock
    private UserContext userContext;

    @Spy
    private UserMapperImpl userMapper;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

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

    @Value("${recommendation-request.once-every.quantity:6}")
    private int quantity;
    @Value("${recommendation-request.once-every.period:MONTHS}")
    private ChronoUnit period;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                recommendationRequestMapper,
                "userMapper",
                userMapper);

        recommendationRequestService = new RecommendationRequestServiceImpl(
                recommendationRequestRepository,
                userRepository,
                recommendationRequestMapper,
                userContext,
                skillRequestRepository,
                Set.of(recommendationRequestMessageContainsFilter,
                        recommendationRequestReceiverIdFilter,
                        recommendationRequestRequesterIdFilter,
                        recommendationRequestStatusFilter),
                quantity,
                period
        );
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

        when(recommendationRequestRepository.findLatestPendingRequest(
                anyLong(),
                anyLong()))
                .thenReturn(Optional.empty());

        Optional<RecommendationRequest> latestPendingRequest =
                recommendationRequestRepository
                        .findLatestPendingRequest(requesterId, receiverId);

        assertFalse(latestPendingRequest.isPresent());
    }

    @Test
    @DisplayName("Create recommendation request earlier than allowed period")
    void testCreateEarlierThanAllowedPeriod() {
        RecommendationRequest recommendationRequest =
                getRecommendationRequest();
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusDays(1));
        long requesterId = recommendationRequest.getRequester().getId();
        long receiverId = recommendationRequest.getReceiver().getId();

        when(recommendationRequestRepository.findLatestPendingRequest(
                anyLong(),
                anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        Optional<RecommendationRequest> latestPendingRequest =
                recommendationRequestRepository
                        .findLatestPendingRequest(requesterId, receiverId);

        CreateRecommendationRequestDto createRecommendationRequestDto =
                new CreateRecommendationRequestDto(
                        receiverId,
                        recommendationRequest.getMessage(),
                        null);

        assertTrue(latestPendingRequest.isPresent());
        assertTrue(latestPendingRequest.get().getCreatedAt()
                .plus(quantity, period).isAfter(LocalDateTime.now()));
        assertThrows(DataValidationException.class, () ->
                recommendationRequestService.create(createRecommendationRequestDto));
    }

    @Test
    @DisplayName("Create recommendation request later than allowed period")
    void testCreateLaterThanAllowedPeriod() {
        RecommendationRequest recommendationRequest = getRecommendationRequest();
        recommendationRequest.setCreatedAt(LocalDateTime.now()
                .minusMonths(quantity).minusDays(1));
        long requesterId = recommendationRequest.getRequester().getId();
        long receiverId = recommendationRequest.getReceiver().getId();

        when(recommendationRequestRepository.findLatestPendingRequest(
                anyLong(),
                anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        Optional<RecommendationRequest> latestPendingRequest =
                recommendationRequestRepository
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
        CreateRecommendationRequestDto createRecommendationRequestDto =
                new CreateRecommendationRequestDto(
                        receiverId,
                        "some message",
                        skillIds);

        RecommendationRequest recommendationRequestForSave =
                recommendationRequestMapper
                        .toRecommendationRequest(createRecommendationRequestDto);

        verify(recommendationRequestMapper, times(1))
                .toRecommendationRequest(createRecommendationRequestDto);

        when(userRepository.getByIdOrThrow(requesterId))
                .thenReturn(User.builder().id(requesterId).build());

        recommendationRequestForSave.setRequester(userRepository
                .getByIdOrThrow(requesterId));

        when(userRepository.getByIdOrThrow(receiverId))
                .thenReturn(User.builder().id(receiverId).build());

        recommendationRequestForSave.setReceiver(userRepository
                .getByIdOrThrow(receiverId));
        recommendationRequestForSave.setStatus(PENDING);
        recommendationRequestForSave.setSkills(new ArrayList<>());

        RecommendationRequest recommendationRequest = recommendationRequestForSave;
        recommendationRequest.setId(111L);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());

        when(recommendationRequestRepository.save(recommendationRequestForSave))
                .thenReturn(recommendationRequest);

        recommendationRequestRepository.save(recommendationRequestForSave);

        when(skillRequestRepository.create(anyLong(), anyLong()))
                .thenAnswer(invocation ->
                        new SkillRequest(
                                invocation.getArgument(1),
                                null,
                                null));

        for (long skillId : createRecommendationRequestDto.skillIds()) {
            SkillRequest skillRequest = skillRequestRepository
                    .create(recommendationRequest.getId(), skillId);
            recommendationRequest.addSkillRequest(skillRequest);
        }

        RecommendationRequestDto recommendationRequestDto =
                recommendationRequestMapper.toRecommendationRequestDto(recommendationRequest);

        verify(recommendationRequestMapper, times(1))
                .toRecommendationRequestDto(recommendationRequest);

        assertNotNull(createRecommendationRequestDto.skillIds());
        assertEquals(recommendationRequestDto.id(),
                recommendationRequest.getId());
        assertEquals(recommendationRequestDto.message(),
                recommendationRequest.getMessage());
        assertEquals(recommendationRequestDto.requester().id(),
                recommendationRequest.getRequester().getId());
        assertEquals(recommendationRequestDto.receiver().id(),
                recommendationRequest.getReceiver().getId());
        assertEquals(recommendationRequestDto.status(),
                recommendationRequest.getStatus());
        assertEquals(recommendationRequestDto.createdAt(),
                recommendationRequest.getCreatedAt());
        assertEquals(recommendationRequestDto.updatedAt(),
                recommendationRequest.getUpdatedAt());

    }

    @Test
    @DisplayName("Get all recommendation requests by filters")
    void testGetByFilters() {
        List<RecommendationRequest> recommendationRequests = List.of(
                RecommendationRequest.builder()
                        .requester(User.builder().id(1L).build())
                        .receiver(User.builder().id(2L).build())
                        .message("some message")
                        .status(ACCEPTED)
                        .build(),
                RecommendationRequest.builder()
                        .requester(User.builder().id(2L).build())
                        .receiver(User.builder().id(3L).build())
                        .message("other message")
                        .status(PENDING)
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
                .thenAnswer((Answer<Stream<RecommendationRequest>>) invocation -> {
                    Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                    return recommendationRequestStream.filter(recommendationRequest ->
                            recommendationRequest.getMessage().toLowerCase().contains("Message".toLowerCase()));
                });

        when(recommendationRequestRequesterIdFilter.apply(any(), any()))
                .thenAnswer((Answer<Stream<RecommendationRequest>>) invocation -> {
                    Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                    return recommendationRequestStream.filter(recommendationRequest ->
                            recommendationRequest.getRequester().getId().equals(2L));
                });

        when(recommendationRequestReceiverIdFilter.apply(any(), any()))
                .thenAnswer((Answer<Stream<RecommendationRequest>>) invocation -> {
                    Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                    return recommendationRequestStream.filter(recommendationRequest ->
                            recommendationRequest.getReceiver().getId().equals(3L));
                });

        when(recommendationRequestStatusFilter.apply(any(), any()))
                .thenAnswer((Answer<Stream<RecommendationRequest>>) invocation -> {
                    Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                    return recommendationRequestStream.filter(recommendationRequest ->
                            recommendationRequest.getStatus().equals(PENDING));
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
        RecommendationRequest request = getRecommendationRequest();
        long requestId = request.getId();

        when(recommendationRequestRepository.getByIdOrThrow(requestId))
                .thenReturn(request);

        RecommendationRequest recommendationRequest =
                recommendationRequestRepository.getByIdOrThrow(requestId);

        RecommendationRequestDto result = recommendationRequestMapper
                .toRecommendationRequestDto(recommendationRequest);

        verify(recommendationRequestMapper, times(1))
                .toRecommendationRequestDto(recommendationRequest);

        assertEquals(recommendationRequest.getId(), result.id());
        assertEquals(recommendationRequest.getRequester().getId(),
                result.requester().id());
        assertEquals(recommendationRequest.getReceiver().getId(),
                result.receiver().id());
        assertEquals(recommendationRequest.getMessage(), result.message());
        assertEquals(recommendationRequest.getStatus(), result.status());
    }

    @Test
    @DisplayName("Get recommendation request by invalid ID")
    void testGetByIdNotFound() {
        long invalidId = 999L;
        when(recommendationRequestRepository.getByIdOrThrow(invalidId))
                .thenAnswer(invocation -> {
                    throw new EntityNotFoundException(String.format(
                            "Recommendation request %d not found", invalidId));
                });

        assertThrows(EntityNotFoundException.class, () ->
                recommendationRequestService.getById(invalidId));
    }

    @Test
    @DisplayName("Accept recommendation request with wrong receiver")
    void testAcceptWithWrongReceiver() {
        when(userContext.getUserId()).thenReturn(5L);

        long currentReceiverId = userContext.getUserId();
        RecommendationRequest request = getRecommendationRequest();
        long requestId = request.getId();

        when(recommendationRequestRepository.getByIdOrThrow(requestId))
                .thenReturn(request);

        long receiverId = recommendationRequestRepository
                .getByIdOrThrow(requestId)
                .getReceiver().getId();

        assertNotEquals(currentReceiverId, receiverId);
        assertThrows(ForbiddenException.class, () ->
                recommendationRequestService.accept(requestId));
    }

    @Test
    @DisplayName("Accept recommendation request with wrong status")
    void testAcceptWithWrongStatus() {
        RecommendationRequest request = getRecommendationRequest();
        long requestId = request.getId();
        request.setStatus(REJECTED);

        when(recommendationRequestRepository.getByIdOrThrow(requestId))
                .thenReturn(request);

        RequestStatus status = recommendationRequestRepository
                .getByIdOrThrow(requestId).getStatus();

        assertNotEquals(PENDING, status);
        assertThrows(ForbiddenException.class, () ->
                recommendationRequestService.accept(requestId));
    }

    @Test
    @DisplayName("Accept recommendation request with valid data")
    void testAcceptWithValidData() {
        RecommendationRequest request = getRecommendationRequest();
        long requestId = request.getId();

        when(recommendationRequestRepository.getByIdOrThrow(requestId))
                .thenReturn(request);

        RecommendationRequest recommendationRequest = recommendationRequestRepository
                .getByIdOrThrow(requestId);
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        recommendationRequestRepository.save(recommendationRequest);

        verify(recommendationRequestRepository, times(1))
                .save(recommendationRequest);

        assertEquals(requestId, recommendationRequest.getId());
        assertEquals(ACCEPTED, recommendationRequest.getStatus());
    }

    @Test
    @DisplayName("Reject recommendation request with wrong receiver")
    void testRejectWithWrongReceiver() {
        when(userContext.getUserId()).thenReturn(5L);

        long currentReceiverId = userContext.getUserId();
        RecommendationRequest request = getRecommendationRequest();
        long requestId = request.getId();
        RejectionDto rejection = new RejectionDto("Rejection reason");

        when(recommendationRequestRepository.getByIdOrThrow(requestId))
                .thenReturn(request);

        long receiverId = recommendationRequestRepository
                .getByIdOrThrow(requestId)
                .getReceiver().getId();

        assertNotEquals(currentReceiverId, receiverId);
        assertThrows(ForbiddenException.class, () ->
                recommendationRequestService.reject(requestId, rejection));
    }

    @Test
    @DisplayName("Reject recommendation request with wrong status")
    void testRejectWithWrongStatus() {
        RecommendationRequest request = getRecommendationRequest();
        long requestId = request.getId();
        request.setStatus(ACCEPTED);
        RejectionDto rejection = new RejectionDto("Rejection reason");

        when(recommendationRequestRepository.getByIdOrThrow(requestId))
                .thenReturn(request);

        RequestStatus status = recommendationRequestRepository
                .getByIdOrThrow(requestId).getStatus();

        assertNotEquals(PENDING, status);
        assertThrows(ForbiddenException.class, () ->
                recommendationRequestService.reject(requestId, rejection));
    }

    @Test
    @DisplayName("Reject recommendation request with valid data")
    void testRejectWithValidData() {
        RecommendationRequest request = getRecommendationRequest();
        long requestId = request.getId();
        RejectionDto rejection = new RejectionDto("Rejection reason");

        when(recommendationRequestRepository.getByIdOrThrow(requestId))
                .thenReturn(request);

        RecommendationRequest recommendationRequest = recommendationRequestRepository
                .getByIdOrThrow(requestId);
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.reason());
        recommendationRequestRepository.save(recommendationRequest);

        verify(recommendationRequestRepository).save(recommendationRequest);

        assertEquals(requestId, recommendationRequest.getId());
        assertEquals(REJECTED, recommendationRequest.getStatus());
        assertEquals(rejection.reason(), recommendationRequest.getRejectionReason());
    }

    private RecommendationRequest getRecommendationRequest() {
        return new RecommendationRequest(
                1L,
                User.builder().id(2L).build(),
                User.builder().id(3L).build(),
                "Test Message",
                PENDING,
                null,
                null,
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}