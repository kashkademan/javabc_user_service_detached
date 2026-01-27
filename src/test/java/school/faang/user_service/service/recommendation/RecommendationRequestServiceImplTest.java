package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.messages.kafka.publusher.RecommendationRequestPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceImplTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private RecommendationRequestPublisher publisher;

    @InjectMocks
    private RecommendationRequestServiceImpl service;

    private static final int LIMIT_MONTHS = 6;
    private User requester;
    private User receiver;
    private UserDto requesterDto;
    private UserDto receiverDto;

    @BeforeEach
    void setUp() {
        try {
            var field = RecommendationRequestServiceImpl.class.getDeclaredField("limit");
            field.setAccessible(true);
            field.setInt(service, LIMIT_MONTHS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        requester = new User();
        requester.setId(1L);
        requester.setUsername("requester");

        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("receiver");

        requesterDto = new UserDto(
                1L, "requester", null, null, null, null, null);
        receiverDto = new UserDto(
                2L, "receiver", null, null, null, null, null);
    }

    @Test
    @DisplayName("create: успешное создание запроса на рекомендацию")
    void create_shouldCreateRecommendationRequestSuccessfully() {
        Long requesterId = requester.getId();
        Long receiverId = receiver.getId();

        given(userContext.getUserId()).willReturn(requesterId);
        given(recommendationRequestRepository.findLatestRequest(requesterId, receiverId))
                .willReturn(Optional.empty());

        given(userRepository.getByIdOrThrow(requesterId)).willReturn(requester);
        given(userRepository.getByIdOrThrow(receiverId)).willReturn(receiver);

        RecommendationRequest request = new RecommendationRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        String message = "Please recommend me";
        request.setMessage(message);
        request.setStatus(RequestStatus.PENDING);

        RecommendationRequest savedRequest = new RecommendationRequest();
        savedRequest.setId(100L);
        savedRequest.setRequester(requester);
        savedRequest.setReceiver(receiver);
        savedRequest.setMessage(message);
        savedRequest.setStatus(RequestStatus.PENDING);
        savedRequest.setCreatedAt(LocalDateTime.now());
        savedRequest.setUpdatedAt(LocalDateTime.now());

        CreateRecommendationRequestDto createDto = new CreateRecommendationRequestDto(receiverId, message);

        given(recommendationRequestMapper.toRecommendationRequest(createDto)).willReturn(request);
        given(recommendationRequestRepository.save(request)).willReturn(savedRequest);

        RecommendationRequestDto expectedDto = RecommendationRequestDto.builder()
                .id(100L)
                .message(message)
                .requester(requesterDto)
                .receiver(receiverDto)
                .status(RequestStatus.PENDING)
                .createdAt(savedRequest.getCreatedAt())
                .updatedAt(savedRequest.getUpdatedAt())
                .build();

        given(recommendationRequestMapper.toRecommendationRequestDto(savedRequest))
                .willReturn(expectedDto);

        RecommendationRequestDto result = service.create(createDto);

        assertThat(result).isEqualTo(expectedDto);
        verify(recommendationRequestRepository).save(request);
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getReceiver()).isEqualTo(receiver);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.PENDING);
    }

    @Test
    @DisplayName("getByFilters: успешная фильтрация запросов")
    void getByFilters_shouldReturnFilteredRequests() {
        Long requesterId = 1L;
        Long receiverId = 2L;
        String messagePattern = "recommend";
        RequestStatus status = RequestStatus.PENDING;

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setId(1L);
        RecommendationRequest request2 = new RecommendationRequest();
        request2.setId(2L);
        List<RecommendationRequest> requests = List.of(request1, request2);

        given(recommendationRequestRepository.findByFilters(
                requesterId, receiverId, messagePattern, status
        )).willReturn(requests);

        RecommendationRequestDto dto1 = RecommendationRequestDto.builder().id(1L).build();
        RecommendationRequestDto dto2 = RecommendationRequestDto.builder().id(2L).build();

        given(recommendationRequestMapper.toRecommendationRequestDto(request1)).willReturn(dto1);
        given(recommendationRequestMapper.toRecommendationRequestDto(request2)).willReturn(dto2);

        RecommendationRequestFilterDto filters = new RecommendationRequestFilterDto(
                requesterId, receiverId, messagePattern, status
        );

        List<RecommendationRequestDto> result = service.getByFilters(filters);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
    }

    @Test
    @DisplayName("getByFilters: фильтрация с null параметрами")
    void getByFilters_shouldHandleNullFilters() {
        RecommendationRequest request = new RecommendationRequest();
        request.setId(1L);
        List<RecommendationRequest> requests = List.of(request);

        given(recommendationRequestRepository.findByFilters(
                null, null, null, null))
                .willReturn(requests);

        RecommendationRequestDto dto = RecommendationRequestDto.builder().id(1L).build();
        given(recommendationRequestMapper.toRecommendationRequestDto(request)).willReturn(dto);

        RecommendationRequestFilterDto filters = new RecommendationRequestFilterDto(
                null, null, null, null
        );

        List<RecommendationRequestDto> result = service.getByFilters(filters);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
    }

    @Test
    @DisplayName("getById: успешное получение запроса по ID")
    void getById_shouldReturnRequestById() {
        long requestId = 100L;
        RecommendationRequest request = new RecommendationRequest();
        request.setId(requestId);

        given(recommendationRequestRepository.getByIdOrThrow(requestId)).willReturn(request);

        RecommendationRequestDto expectedDto = RecommendationRequestDto.builder()
                .id(requestId)
                .build();
        given(recommendationRequestMapper.toRecommendationRequestDto(request)).willReturn(expectedDto);

        RecommendationRequestDto result = service.getById(requestId);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("getById: ошибка при отсутствии запроса")
    void getById_shouldThrowWhenRequestNotFound() {
        long requestId = 999L;
        given(recommendationRequestRepository.getByIdOrThrow(requestId))
                .willThrow(new EntityNotFoundException("Recommendation request not found"));

        assertThatThrownBy(() -> service.getById(requestId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("accept: успешное принятие запроса")
    void accept_shouldAcceptRequestSuccessfully() {
        long requestId = 100L;
        Long receiverId = receiver.getId();

        given(userContext.getUserId()).willReturn(receiverId);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(requestId);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        given(recommendationRequestRepository.getByIdOrThrow(requestId)).willReturn(request);

        service.accept(requestId);

        assertThat(request.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
        verify(recommendationRequestRepository).save(request);
    }

    @Test
    @DisplayName("accept: ошибка при попытке принять не свой запрос")
    void accept_shouldThrowWhenNotReceiver() {
        long requestId = 100L;
        Long wrongUserId = 3L;

        given(userContext.getUserId()).willReturn(wrongUserId);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(requestId);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        given(recommendationRequestRepository.getByIdOrThrow(requestId)).willReturn(request);

        assertThatThrownBy(() -> service.accept(requestId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Access denied");

        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("accept: ошибка при попытке принять не PENDING запрос")
    void accept_shouldThrowWhenNotPendingStatus() {
        long requestId = 100L;
        Long receiverId = receiver.getId();

        given(userContext.getUserId()).willReturn(receiverId);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(requestId);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.ACCEPTED);

        given(recommendationRequestRepository.getByIdOrThrow(requestId)).willReturn(request);

        assertThatThrownBy(() -> service.accept(requestId))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("Request can be accepted only if it is in PENDING status");

        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("reject: успешный отказ от запроса")
    void reject_shouldRejectRequestSuccessfully() {
        long requestId = 100L;
        Long receiverId = receiver.getId();

        given(userContext.getUserId()).willReturn(receiverId);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(requestId);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        given(recommendationRequestRepository.getByIdOrThrow(requestId)).willReturn(request);

        String rejectionReason = "I don't know this person well enough";

        RejectionDto rejectionDto = new RejectionDto(rejectionReason);

        service.reject(requestId, rejectionDto);

        assertThat(request.getStatus()).isEqualTo(RequestStatus.REJECTED);
        assertThat(request.getRejectionReason()).isEqualTo(rejectionReason);
        verify(recommendationRequestRepository).save(request);
    }

    @Test
    @DisplayName("reject: ошибка при попытке отклонить не свой запрос")
    void reject_shouldThrowWhenNotReceiver() {
        long requestId = 100L;
        Long wrongUserId = 3L;

        given(userContext.getUserId()).willReturn(wrongUserId);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(requestId);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        given(recommendationRequestRepository.getByIdOrThrow(requestId)).willReturn(request);

        RejectionDto rejectionDto = new RejectionDto("Reason");

        assertThatThrownBy(() -> service.reject(requestId, rejectionDto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Access denied");

        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("reject: ошибка при попытке отклонить не PENDING запрос")
    void reject_shouldThrowWhenNotPendingStatus() {
        long requestId = 100L;
        Long receiverId = receiver.getId();

        given(userContext.getUserId()).willReturn(receiverId);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(requestId);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.REJECTED);

        given(recommendationRequestRepository.getByIdOrThrow(requestId)).willReturn(request);

        RejectionDto rejectionDto = new RejectionDto("Reason");

        assertThatThrownBy(() -> service.reject(requestId, rejectionDto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("Request can be rejected only if it is in PENDING status");

        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("reject: успешный отказ с пустой причиной")
    void reject_shouldHandleEmptyRejectionReason() {
        long requestId = 100L;
        Long receiverId = receiver.getId();

        given(userContext.getUserId()).willReturn(receiverId);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(requestId);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        given(recommendationRequestRepository.getByIdOrThrow(requestId)).willReturn(request);

        RejectionDto rejectionDto = new RejectionDto("");

        service.reject(requestId, rejectionDto);

        assertThat(request.getStatus()).isEqualTo(RequestStatus.REJECTED);
        assertThat(request.getRejectionReason()).isEmpty();
        verify(recommendationRequestRepository).save(request);
    }
}