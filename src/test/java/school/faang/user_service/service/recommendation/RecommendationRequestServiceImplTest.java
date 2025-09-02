package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestViewDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.publisher.RecommendationRequestedEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для проверки функциональности {@link RecommendationRequestServiceImpl},
 * <p>
 * Проверяет корректность работы методов сервиса:
 * <ul>
 *     <li>Создание запроса на рекомендацию (валидация, сохранение)</li>
 *     <li>Получение запроса по ID</li>
 *     <li>Фильтрация запросов</li>
 *     <li>Принятие/отклонение запроса</li>
 * </ul>
 * </p>
 *
 * @author Linempy
 * @since 11.07.2025
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("Проверка сервиса для запроса на рекомендацию")
public class RecommendationRequestServiceImplTest {

    @Mock
    private RecommendationRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Spy
    private RecommendationRequestMapperImpl mapper;

    @Mock
    private UserContext userContext;

    @Mock
    private RecommendationRequestedEventPublisher publisher;

    @Mock
    private RecommendationRequestFilter messageFilter;
    @Mock
    private RecommendationRequestFilter receiverIdFilter;
    @Mock
    private RecommendationRequestFilter requestIdFilter;
    @Mock
    private RecommendationRequestFilter statusFilter;

    @Captor
    private ArgumentCaptor<RecommendationRequest> captor;

    RecommendationRequestServiceImpl service;

    @BeforeEach
    public void setUp() {
        List<RecommendationRequestFilter> filters = List.of(
                messageFilter,
                receiverIdFilter,
                requestIdFilter,
                statusFilter
        );
        service = new RecommendationRequestServiceImpl(
                requestRepository,
                userRepository,
                skillRequestRepository,
                mapper,
                filters,
                publisher,
                userContext
        );
    }

    @Test
    @DisplayName("Проверка на то, что пользователь отправил запрос сам себе")
    public void testCreateWithSelfRecommendation() {
        long receiverId = 1L;
        RecommendationRequestCreateDto dto = new RecommendationRequestCreateDto(
                "Сообщение",
                receiverId,
                List.of(10L, 29L)
        );

        when(userContext.getUserId()).thenReturn(receiverId);

        assertThrows(ForbiddenException.class, () -> service.create(dto));
    }

    @Test
    @DisplayName("Проверка частоты отправки запроса одному и тому же человеку")
    public void testCreateWithCooldownPeriod() {
        long receiverId = 1L;
        long requesterId = 2L;
        RecommendationRequestCreateDto dto = new RecommendationRequestCreateDto(
                "Сообщение",
                receiverId,
                List.of(10L, 29L)
        );

        when(userContext.getUserId()).thenReturn(requesterId);
        when(requestRepository
                .existsByRequesterIdAndReceiverIdAndCreatedAtAfter(
                        eq(requesterId),
                        eq(receiverId),
                        any(LocalDateTime.class)
                )).thenReturn(true);

        assertThrows(ForbiddenException.class, () -> service.create(dto));
    }

    @Test
    @DisplayName("Проверка успешного сохранения запроса рекомендации в БД")
    public void testCreateSuccessful() {
        long receiverId = 1L;
        long requesterId = 2L;
        String message = "Какое-то сообщение";
        List<Long> skillIds = List.of(10L, 20L);

        User receiver = new User();
        receiver.setId(receiverId);

        User requester = new User();
        requester.setId(requesterId);

        RecommendationRequestCreateDto dto = new RecommendationRequestCreateDto(
                message,
                receiverId,
                skillIds
        );
        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.getByIdOrThrow(dto.receiverId())).thenReturn(receiver);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(requester);

        long requestId = 1L;
        when(requestRepository.save(captor.capture())).thenAnswer(
                arg -> {
                    RecommendationRequest request = arg.getArgument(0);
                    request.setId(requestId);
                    return request;
                }
        );

        AtomicLong skillRequestId = new AtomicLong(1L);
        skillIds.forEach(
                id -> {
                    SkillRequest mockSkill = new SkillRequest();
                    mockSkill.setId(skillRequestId.getAndIncrement());
                    Skill skill = new Skill();
                    skill.setId(id);
                    mockSkill.setSkill(skill);
                    when(skillRequestRepository.create(requestId, id)).thenReturn(mockSkill);
                }
        );
        RecommendationRequestViewDto resultDto = service.create(dto);
        verify(requestRepository, times(1)).save(captor.capture());
        skillIds.forEach(id -> verify(skillRequestRepository, times(1)).create(requestId, id));
        assertEquals(message, resultDto.message());
        assertEquals(requester.getId(), resultDto.requester().id());
        assertEquals(receiver.getId(), resultDto.receiver().id());
        assertEquals(RequestStatus.PENDING, resultDto.status());
        assertEquals(dto.skillIds(), resultDto.skillIds());

        RecommendationRequest savedRequest = captor.getValue();
        assertEquals(message, savedRequest.getMessage());
        assertEquals(receiverId, savedRequest.getReceiver().getId());
        assertEquals(skillIds, savedRequest.getSkills().stream()
                .map(skillReq -> skillReq.getSkill().getId())
                .toList());
        assertEquals(RequestStatus.PENDING, savedRequest.getStatus());
    }

    @Test
    @DisplayName("Проверка на получение DTO запроса рекомендации по id")
    public void testGetByIdSuccessful() {
        long requestId = 1L;

        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(requestId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);
        request.setMessage("Test");

        RecommendationRequestViewDto expectedDto = new RecommendationRequestViewDto(
                requestId,
                "Test",
                new UserViewDto(1L, null, null, null, null, null),
                new UserViewDto(2L, null, null, null, null, null),
                RequestStatus.PENDING,
                List.of(),
                LocalDateTime.now()
        );

        when(requestRepository.getByIdOrThrow(requestId)).thenReturn(request);
        doReturn(expectedDto).when(mapper).toViewDto(request);

        RecommendationRequestViewDto actualDto = service.getById(requestId);

        verify(requestRepository, times(1)).getByIdOrThrow(requestId);
        verify(mapper, times(1)).toViewDto(request);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Проверка получения DTO запросов на рекомендаций только по \"message\"-фильтру")
    public void testGetByFiltersWhenIsApplicableOnlyOne() {
        String searchPhrase = "test";
        RecommendationRequestFilterDto filterDto = new RecommendationRequestFilterDto(
                null, null, searchPhrase, null
        );
        createTestRequestsAndMockFindAll();

        mockFiltersApplicability(filterDto, Map.of(
                messageFilter, true,
                receiverIdFilter, false,
                requestIdFilter, false,
                statusFilter, false
        ));

        mockFilterApply(messageFilter, request ->
                request.getMessage().toLowerCase()
                        .contains(filterDto.messageContains().toLowerCase())
        );

        List<RecommendationRequestViewDto> result = service.getByFilters(filterDto);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto ->
                dto.message().toLowerCase().contains(searchPhrase)
        ));
    }

    @Test
    @DisplayName("Проверка получения DTO запросов на рекомендаций по 2 фильтрам")
    public void testGetByFiltersWhenIsApplicableTwoFilters() {
        String searchPhrase = "test";
        Long targetReceiverId = 1L;
        RecommendationRequestFilterDto filterDto = new RecommendationRequestFilterDto(
                null, targetReceiverId, searchPhrase, null
        );

        createTestRequestsAndMockFindAll();

        mockFiltersApplicability(filterDto, Map.of(
                messageFilter, true,
                receiverIdFilter, true,
                requestIdFilter, false,
                statusFilter, false
        ));

        mockFilterApply(messageFilter, request ->
                request.getMessage().toLowerCase()
                        .contains(filterDto.messageContains().toLowerCase())
        );

        mockFilterApply(receiverIdFilter, request ->
                request.getReceiver().getId().equals(targetReceiverId)
        );

        List<RecommendationRequestViewDto> result = service.getByFilters(filterDto);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(dto ->
                dto.message().toLowerCase().contains(searchPhrase))
        );
        assertTrue(result.stream().allMatch(dto ->
                dto.receiver().id().equals(targetReceiverId))
        );
    }

    @Test
    @DisplayName("Проверка получения DTO запросов на рекомендаций по 3 фильтрам")
    public void testGetByFiltersWhenIsApplicableThreeFiltersWhenStatusIsRejected() {
        String searchPhrase = "test";
        Long targetReceiverId = 1L;
        RequestStatus status = RequestStatus.REJECTED;
        RecommendationRequestFilterDto filterDto = new RecommendationRequestFilterDto(
                null, targetReceiverId, searchPhrase, status
        );
        createTestRequestsAndMockFindAll();

        mockFiltersApplicability(filterDto, Map.of(
                messageFilter, true,
                receiverIdFilter, true,
                requestIdFilter, false,
                statusFilter, true
        ));

        mockFilterApply(messageFilter, request ->
                request.getMessage().toLowerCase()
                        .contains(filterDto.messageContains().toLowerCase())
        );

        mockFilterApply(receiverIdFilter, request ->
                request.getReceiver().getId().equals(targetReceiverId)
        );

        mockFilterApply(statusFilter, request ->
                request.getStatus() == status
        );

        List<RecommendationRequestViewDto> result = service.getByFilters(filterDto);
        assertEquals(0, result.size());
        assertTrue(result.stream().allMatch(dto ->
                dto.message().toLowerCase().contains(searchPhrase))
        );
        assertTrue(result.stream().allMatch(dto ->
                dto.receiver().id().equals(targetReceiverId))
        );
    }

    @Test
    @DisplayName("reject() выбрасывает ForbiddenException, когда пользователь не является получателем запроса")
    public void testRejectWhenUserIsNotReceiver() {
        long userContextId = 1L;
        long receiverId = 2L;
        String reason = "Потому что гладиолус";
        RejectionDto rejection = new RejectionDto(reason);
        RecommendationRequest request = RecommendationRequest.builder()
                .receiver(User.builder().id(receiverId).build())
                .build();

        when(userContext.getUserId()).thenReturn(userContextId);
        when(requestRepository.getByIdOrThrow(receiverId)).thenReturn(request);

        assertThrows(ForbiddenException.class, () -> service.reject(receiverId, rejection));
        verify(requestRepository, times(1)).getByIdOrThrow(receiverId);
    }

    @Test
    @DisplayName("reject() выбрасывает ForbiddenException, когда пользователь не является получателем запроса")
    public void testRejectWhenIsNotPendingStatus() {
        String reason = "Потому что гладиолус";
        RejectionDto rejection = new RejectionDto(reason);
        long receiverId = 2L;
        RecommendationRequest request = RecommendationRequest.builder()
                .status(RequestStatus.ACCEPTED)
                .receiver(User.builder().id(receiverId).build())
                .build();

        when(userContext.getUserId()).thenReturn(receiverId);
        when(requestRepository.getByIdOrThrow(receiverId)).thenReturn(request);

        assertThrows(ForbiddenException.class, () -> service.reject(receiverId, rejection));
        verify(requestRepository, times(1)).getByIdOrThrow(receiverId);
    }

    @Test
    @DisplayName("Успешное отклонение запроса")
    public void testRejectSuccessful() {
        String reason = "Потому что гладиолус";
        RejectionDto rejection = new RejectionDto(reason);
        long receiverId = 2L;
        RecommendationRequest request = RecommendationRequest.builder()
                .status(RequestStatus.PENDING)
                .receiver(User.builder().id(receiverId).build())
                .build();

        when(userContext.getUserId()).thenReturn(receiverId);
        when(requestRepository.getByIdOrThrow(receiverId)).thenReturn(request);

        service.reject(receiverId, rejection);

        verify(requestRepository, times(1)).getByIdOrThrow(receiverId);
        verify(requestRepository, times(1)).save(request);
        assertEquals(RequestStatus.REJECTED, request.getStatus());
        assertEquals(reason, request.getRejectionReason());
    }

    @Test
    @DisplayName("accept() выбрасывает ForbiddenException, когда пользователь не является получателем запроса")
    public void testAcceptWhenUserIsNotReceiver() {
        long userContextId = 1L;
        long receiverId = 2L;
        RecommendationRequest request = RecommendationRequest.builder()
                .receiver(User.builder().id(receiverId).build())
                .build();

        when(userContext.getUserId()).thenReturn(userContextId);
        when(requestRepository.getByIdOrThrow(receiverId)).thenReturn(request);

        assertThrows(ForbiddenException.class, () -> service.accept(receiverId));
        verify(requestRepository, times(1)).getByIdOrThrow(receiverId);
    }

    @Test
    @DisplayName("accept() выбрасывает ForbiddenException, когда пользователь не является получателем запроса")
    public void testAcceptWhenIsNotPendingStatus() {
        long receiverId = 2L;
        RecommendationRequest request = RecommendationRequest.builder()
                .status(RequestStatus.ACCEPTED)
                .receiver(User.builder().id(receiverId).build())
                .build();

        when(userContext.getUserId()).thenReturn(receiverId);
        when(requestRepository.getByIdOrThrow(receiverId)).thenReturn(request);

        assertThrows(ForbiddenException.class, () -> service.accept(receiverId));
        verify(requestRepository, times(1)).getByIdOrThrow(receiverId);
    }

    @Test
    @DisplayName("Успешное принятие запроса")
    public void testAcceptSuccessful() {
        long receiverId = 2L;
        RecommendationRequest request = RecommendationRequest.builder()
                .status(RequestStatus.PENDING)
                .receiver(User.builder().id(receiverId).build())
                .build();

        when(userContext.getUserId()).thenReturn(receiverId);
        when(requestRepository.getByIdOrThrow(receiverId)).thenReturn(request);

        service.accept(receiverId);

        verify(requestRepository, times(1)).getByIdOrThrow(receiverId);
        verify(requestRepository, times(1)).save(request);
        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
    }


    private void createTestRequestsAndMockFindAll() {
        List<RecommendationRequest> requests = List.of(
                createRequest(1L, "Test", 2L, 1L, RequestStatus.PENDING),
                createRequest(2L, "MEssAge", 3L, 1L, RequestStatus.ACCEPTED),
                createRequest(3L, "-- test --", 2L, 3L, RequestStatus.REJECTED),
                createRequest(4L, "T e s T", 5L, 1L, RequestStatus.REJECTED)
        );
        when(requestRepository.findAll()).thenReturn(requests);

    }

    private RecommendationRequest createRequest(
            Long id, String message, Long requesterId, Long receiverId, RequestStatus status) {
        return RecommendationRequest.builder()
                .id(id)
                .message(message)
                .requester(createUser(requesterId))
                .receiver(createUser(receiverId))
                .status(status)
                .build();
    }

    private User createUser(Long id) {
        return User.builder().id(id).build();
    }

    private void mockFiltersApplicability(
            RecommendationRequestFilterDto filterDto,
            Map<RecommendationRequestFilter, Boolean> filterStatuses
    ) {
        filterStatuses.forEach((filter, isApplicable) -> when(filter.isApplicable(filterDto))
                .thenReturn(isApplicable)
        );
    }

    private void mockFilterApply(
            RecommendationRequestFilter filter,
            Predicate<RecommendationRequest> predicate
    ) {
        when(filter.apply(any(), any())).thenAnswer(
                (Answer<Stream<RecommendationRequest>>) invocation -> {
                    Stream<RecommendationRequest> requests = invocation.getArgument(0);
                    return requests
                            .filter(predicate);
                }
        );
    }
}