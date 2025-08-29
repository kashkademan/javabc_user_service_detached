package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.publisher.SaveEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * RecommendationServiceImplTest — тестирование класса {@link RecommendationServiceImpl}.
 *
 * @author bozya
 * @since 18.07.2025
 */
@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private RecommendationFilter filterAuthorId;

    @Mock
    private RecommendationFilter filterContentContains;

    @Mock
    private RecommendationFilter filterReceiverId;

    @Spy
    private RecommendationMapper recommendationMapper;

    @Mock
    private SaveEventPublisher<RecommendationEvent> publisher;

    @InjectMocks
    private RecommendationServiceImpl recommendationServiceImpl;

    @Test
    @DisplayName("Проверка на то, что пользователь-отправитель не является получателем")
    void testCreateAuthorIsNotReceiver() {
        Long authorId = 1L;

        RecommendationCreateDto createDto = new RecommendationCreateDto(authorId, authorId, "Текст рекомендации");

        when(userContext.getUserId()).thenReturn(authorId);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationServiceImpl.create(createDto));

        assertEquals("Вы не можете написать рекомендацию себе!", ex.getMessage());
    }

    @Test
    @DisplayName("Проверка частоты отправки рекомендации")
    void testCreateRecommendationFrequency() {
        Long authorId = 1L;
        Long receiverId = 2L;

        Recommendation lastRec = new Recommendation();
        lastRec.setCreatedAt(LocalDateTime.now().minusMonths(3));

        RecommendationCreateDto createDto = new RecommendationCreateDto(authorId, receiverId, "Текст рекомендации");

        when(userContext.getUserId()).thenReturn(authorId);
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenReturn(Optional.of(lastRec));

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> recommendationServiceImpl.create(createDto));

        assertEquals("Вы не можете писать рекомендации чаще, чем раз в полгода", ex.getMessage());
    }

    @Test
    @DisplayName("Проверка создания рекомендации")
    void testCreateRecommendation() {
        Long authorId = 1L;
        Long receiverId = 2L;
        LocalDate dateOfRecommendation = LocalDate.now();

        RecommendationCreateDto createDto = new
                RecommendationCreateDto(authorId, receiverId, "Рекомендация");

        Recommendation recommendation = new Recommendation();
        recommendation.setId(10L);
        recommendation.setContent(createDto.content());

        RecommendationViewDto expectedDto = new
                RecommendationViewDto(10L, receiverId, authorId, createDto.content(), dateOfRecommendation);

        when(userContext.getUserId()).thenReturn(authorId);

        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenReturn(Optional.empty());

        when(recommendationRepository.create(authorId, receiverId, createDto.content()))
                .thenReturn(recommendation.getId());

        when(recommendationRepository.findById(recommendation.getId())).thenReturn(Optional.of(recommendation));

        when(recommendationMapper.toViewDto(recommendation)).thenReturn(expectedDto);

        RecommendationViewDto actualDto = recommendationServiceImpl.create(createDto);
        RecommendationEvent event = new RecommendationEvent(
                authorId,
                receiverId,
                10L,
                null
        );

        assertEquals(expectedDto, actualDto);
        verify(recommendationRepository).findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
        verify(recommendationRepository).create(authorId, receiverId, createDto.content());
        verify(recommendationRepository).findById(recommendation.getId());
        verify(recommendationMapper).toViewDto(recommendation);
        verify(publisher).publishAfterCommit(event);
    }

    @Test
    @DisplayName("Успешеная проверка обновления рекомендации")
    void testUpdateRecommendation() {
        long recommendationId = 10L;
        LocalDate dateOfRecommendation = LocalDate.now();

        User author = new User();
        author.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        RecommendationUpdateDto updateDto = new RecommendationUpdateDto(
                    author.getId(),
                    receiver.getId(),
            "Новая рекомендация"
        );

        Recommendation existingRecommendation = Recommendation.builder()
            .id(recommendationId)
            .author(author)
            .receiver(receiver)
            .content("Старая рекомендация")
            .build();

        Recommendation updatedRecommendation = Recommendation.builder()
            .id(recommendationId)
            .author(author)
            .receiver(receiver)
            .content(updateDto.content())
            .build();

        RecommendationViewDto expectedDto = new RecommendationViewDto(
            recommendationId,
            receiver.getId(),
            author.getId(),
            updateDto.content(),
            dateOfRecommendation
        );

        when(userContext.getUserId()).thenReturn(author.getId());

        when(recommendationRepository.findById(recommendationId))
                .thenReturn(Optional.of(existingRecommendation));

        when(recommendationRepository.update(author.getId(), receiver.getId(), updateDto.content()))
                .thenReturn(updatedRecommendation);

        when(recommendationMapper.toViewDto(updatedRecommendation)).thenReturn(expectedDto);

        RecommendationViewDto actualDto = recommendationServiceImpl.update(recommendationId, updateDto);

        assertEquals(expectedDto, actualDto);

        verify(recommendationRepository).findById(recommendationId);
        verify(userContext).getUserId();
        verify(recommendationRepository).update(author.getId(), receiver.getId(), updateDto.content());
        verify(recommendationMapper).toViewDto(updatedRecommendation);
    }

    @Test
    @DisplayName("Проверка успешного удаления рекомендации")
    void testDeleteRecommendation() {
        long recommendationId = 10L;
        long currentUserId = 1L;

        when(userContext.getUserId()).thenReturn(currentUserId);

        recommendationServiceImpl.delete(recommendationId);

        verify(recommendationRepository, times(1))
                .deleteByIdAndAuthor_id(recommendationId, currentUserId);
    }
}
