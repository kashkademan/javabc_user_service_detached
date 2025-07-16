package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestViewDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
 * </ul>
 * Использует {@code Mockito} для мокирования зависимостей
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

    @Captor
    private ArgumentCaptor<RecommendationRequest> captor;

    @InjectMocks
    RecommendationRequestServiceImpl service;

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
                new UserDto(1L, null, null, null, null), // Упрощенный DTO
                new UserDto(2L, null, null, null, null),
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
    @DisplayName("Проверка получения DTO запросов на рекомендаций по ID и фильтрам")
    public void testGetByFiltersSuccessful() {

    }

}