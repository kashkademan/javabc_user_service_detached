package school.faang.user_service.service.recommendation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;
    private final List<RecommendationFilter> filters;

    private void validateAuthorIsNotReceiver(Long authorId, Long receiverId) {
        if (authorId.equals(receiverId)) {
            throw new DataValidationException("Вы не можете написать рекомендацию себе");
        }
    }

    private void validateRecommendationFrequency(Optional<Recommendation> lastRecommendation,
                                                 LocalDateTime sixMonthAgo) {
        if (lastRecommendation.isPresent()
                && lastRecommendation.get().getCreatedAt().isAfter(sixMonthAgo)) {
            throw new ForbiddenException("Вы не можете писать рекомендации чаще, чем раз в полгода");
        }
    }

    private void validateAuthorIsCurrentUser(Long currentId, Long authorId) {
        if (!currentId.equals(authorId)) {
            throw new ForbiddenException("Вы можете редактировать только свои рекомендации");
        }
    }

    @Override
    @Transactional
    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        LocalDateTime sixMonthAgo = LocalDateTime.now().minusMonths(6);

        Long authorId = userContext.getUserId();
        Long receiverId = recommendationDto.receiverId();

        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        validateAuthorIsNotReceiver(authorId, receiverId);
        validateRecommendationFrequency(lastRecommendation, sixMonthAgo);

        Long recommendationId = recommendationRepository.create(
                authorId,
                receiverId,
                recommendationDto.content()
        );

        Recommendation savedRecommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Не удалось найти созданную рекомендацию"));

        log.info("Рекомендация для пользователя {} создана", recommendationDto.receiverId());

        return recommendationMapper.toRecommedationDto(savedRecommendation);
    }

    @Override
    @Transactional
    public RecommendationDto update(long recommendationId, UpdateRecommendationDto updateRecommendationDto) {
        Long currentId = userContext.getUserId();
        Recommendation recommendation = recommendationRepository.findById(updateRecommendationDto.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Рекомендация не найдена"));

        validateAuthorIsCurrentUser(userContext.getUserId(), updateRecommendationDto.getAuthorId());

        recommendation.setContent(updateRecommendationDto.getContent());

        Recommendation updatedRecommendation = recommendationRepository.update(updateRecommendationDto.getAuthorId(),
                updateRecommendationDto.getRecieverId(), updateRecommendationDto.getContent());

        log.info("Рекомендация для пользователя {} обновлена", updateRecommendationDto.getRecieverId());
        return recommendationMapper.toRecommedationDto(updatedRecommendation);
    }

    @Override
    @Transactional
    public void delete(long recommendationId) {
        log.info("Рекомендация {} удалена", recommendationId);
        recommendationRepository.deleteByIdAndAuthor_id(recommendationId, userContext.getUserId());
    }

    @Override
    @Transactional
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filtersDto) {
        var recommendations = recommendationRepository.findAll().stream();
        for (RecommendationFilter filter : filters) {
            if (filter.isApplicable(filtersDto)) {
                recommendations = filter.filter(recommendations, filtersDto);
            }
        }
        return recommendations
                .map(recommendationMapper::toRecommedationDto)
                .toList();
    }
}
