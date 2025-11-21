package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceImplTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper = Mappers
            .getMapper(RecommendationRequestMapperImpl.class);

    @Mock
    private UserContext userContext;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    private final User requester = User.builder()
            .id(1L)
            .username("Иван Иванов")
            .email("ivanov@example.com")
            .phone("12345")
            .aboutMe("About Иван Иванов")
            .build();

    private final User receiver = User.builder()
            .id(2L)
            .username("Петр Петров")
            .email("petrov@example.com")
            .phone("54321")
            .aboutMe("About Петр Петров")
            .build();

    @Test
    void create_ShouldCreateRequestSuccessfully() {
        CreateRecommendationRequestDto createDto = new CreateRecommendationRequestDto(
                "Please write me a recommendation", 1L, 2L);

        RecommendationRequest savedRequest = RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .message(createDto.message())
                .createdAt(LocalDateTime.now())
                .build();

        when(recommendationRequestRepository.findLatestPendingRequest(
                createDto.requesterId(), createDto.receiverId()))
                .thenReturn(Optional.empty());
        when(userRepository.getByIdOrThrow(createDto.requesterId()))
                .thenReturn(requester);
        when(userRepository.getByIdOrThrow(createDto.receiverId()))
                .thenReturn(receiver);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(savedRequest);

        RecommendationRequestDto result = recommendationRequestService.create(createDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(requester.getId(), result.requester().id());
        assertEquals(receiver.getId(), result.receiver().id());
        assertEquals(RequestStatus.PENDING, result.status());
        assertEquals("Please write me a recommendation", result.message());

        verify(recommendationRequestRepository).save(any(RecommendationRequest.class));
    }

    @Test
    void create_ShouldThrowExceptionWhenRequesterAndReceiverAreSame() {

        CreateRecommendationRequestDto sameUserDto = new CreateRecommendationRequestDto(
                "Message",
                1L,
                1L
        );

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.create(sameUserDto)
        );

        assertEquals("Requester and receiver are the same person.", exception.getMessage());
        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowExceptionWhenPendingRequestExistsWithinTimeLimit() {

        CreateRecommendationRequestDto createDto = new CreateRecommendationRequestDto(
                "Please write me a recommendation", 1L, 2L);

        LocalDateTime fixedDate = LocalDateTime.of(2025, 11, 5, 12, 00, 00);
        LocalDateTime recentRequestTime = fixedDate.minusMonths(5);

        RecommendationRequest existingRequest = RecommendationRequest.builder()
                .id(100L)
                .createdAt(recentRequestTime)
                .build();

        when(recommendationRequestRepository.findLatestPendingRequest(
                createDto.requesterId(), createDto.receiverId()))
                .thenReturn(Optional.of(existingRequest));


        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.create(createDto)
        );

        assertEquals("You have already made a request during this period", exception.getMessage());
        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    void getById_ShouldReturnRecommendationRequestSuccessfully() {

        RecommendationRequest request = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .requester(requester).receiver(receiver).build();

        when(recommendationRequestRepository.getByIdOrThrow(1L)).thenReturn(request);


        RecommendationRequestDto result = recommendationRequestService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());

        assertEquals(requester.getId(), result.requester().id());
        assertEquals(requester.getUsername(), result.requester().username());
        assertEquals(receiver.getId(), result.receiver().id());
        assertEquals(receiver.getUsername(), result.receiver().username());

        verify(recommendationRequestRepository).getByIdOrThrow(1L);
        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    void accept_ShouldAcceptRecommendationRequestSuccessfully() {

        RecommendationRequest request = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .requester(requester).receiver(receiver).build();

        when(userContext.getUserId()).thenReturn(receiver.getId());
        when(recommendationRequestRepository.getByIdOrThrow(request.getId()))
                .thenReturn(request);

        recommendationRequestService.accept(request.getId());

        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
        verify(recommendationRequestRepository).save(request);
    }

    @Test
    void accept_ShouldThrowForbiddenExceptionWhenUserIsNotReceiver() {
        Long wrongUserId = 999L;
        RecommendationRequest request = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .requester(requester).receiver(receiver).build();

        when(userContext.getUserId()).thenReturn(wrongUserId);
        when(recommendationRequestRepository.getByIdOrThrow(request.getId())).thenReturn(request);

        assertThrows(
                ForbiddenException.class,
                () -> recommendationRequestService.accept(request.getId())
        );
        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    void accept_ShouldThrowDataValidationExceptionWhenStatusIsNotPending() {
        RecommendationRequest request = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.ACCEPTED)
                .requester(requester).receiver(receiver).build();

        when(userContext.getUserId()).thenReturn(receiver.getId());
        when(recommendationRequestRepository.getByIdOrThrow(request.getId()))
                .thenReturn(request);

        assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.accept(request.getId())
        );
        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    void reject_ShouldRejectRecommendationRequestSuccessfully() {
        RejectionDto dto = new RejectionDto("Слишком занят, нет времени");
        RecommendationRequest request = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .requester(requester).receiver(receiver).build();


        when(userContext.getUserId()).thenReturn(receiver.getId());
        when(recommendationRequestRepository.getByIdOrThrow(request.getId()))
                .thenReturn(request);

        recommendationRequestService.reject(request.getId(), dto);

        assertEquals(RequestStatus.REJECTED, request.getStatus());
        verify(recommendationRequestRepository).save(request);
    }

    @Test
    void reject_ShouldThrowForbiddenExceptionWhenUserIsNotReceiver() {
        RejectionDto dto = new RejectionDto("Слишком занят, нет времени");
        Long wrongUserId = 999L;
        RecommendationRequest request = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .requester(requester).receiver(receiver).build();

        when(userContext.getUserId()).thenReturn(wrongUserId);
        when(recommendationRequestRepository.getByIdOrThrow(request.getId())).thenReturn(request);

        assertThrows(
                ForbiddenException.class,
                () -> recommendationRequestService.reject(request.getId(), dto)
        );
        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    void reject_ShouldThrowDataValidationExceptionWhenStatusIsNotPending() {
        RejectionDto dto = new RejectionDto("Слишком занят, нет времени");
        RecommendationRequest request = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.ACCEPTED)
                .requester(requester).receiver(receiver).build();

        when(userContext.getUserId()).thenReturn(receiver.getId());
        when(recommendationRequestRepository.getByIdOrThrow(request.getId()))
                .thenReturn(request);

        assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.reject(request.getId(), dto)
        );
        verify(recommendationRequestRepository, never()).save(any());
    }
}