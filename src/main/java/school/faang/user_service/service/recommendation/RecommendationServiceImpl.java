package school.faang.user_service.service.recommendation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
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

    @Override
    @Transactional
    public RecommendationViewDto create(RecommendationCreateDto recommendationDto) {
        LocalDateTime sixMonthAgo = LocalDateTime.now().minusMonths(6);

        Long authorId = userContext.getUserId();
        Long receiverId = recommendationDto.receiverId();

        validateAuthorIsNotReceiver(authorId, receiverId);

        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        validateRecommendationFrequency(lastRecommendation, sixMonthAgo);

        Long recommendationId = recommendationRepository.create(
                authorId,
                receiverId,
                recommendationDto.content()
        );

        Recommendation savedRecommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new EntityNotFoundException("Не удалось найти созданную рекомендацию"));

        log.info("Рекомендация для пользователя {} создана", recommendationDto.receiverId());

        return recommendationMapper.toViewDto(savedRecommendation);
    }

    @Override
    @Transactional
    public RecommendationViewDto update(long recommendationId, RecommendationUpdateDto updateRecommendationDto) {
        Long currentId = userContext.getUserId();
        Long receiverId = updateRecommendationDto.receiverId();
        Long authorId = updateRecommendationDto.authorId();

        recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new EntityNotFoundException("Рекомендация не найдена"));

        validateAuthorIsCurrentUser(currentId, authorId);

        Recommendation updatedRecommendation = recommendationRepository.update(authorId,
                receiverId, updateRecommendationDto.content());

        log.info("Рекомендация для пользователя {} обновлена", receiverId);
        return recommendationMapper.toViewDto(updatedRecommendation);
    }

    @Override
    @Transactional
    public void delete(long recommendationId) {
        log.info("Рекомендация {} удалена", recommendationId);
        recommendationRepository.deleteByIdAndAuthor_id(recommendationId, userContext.getUserId());
    }

    @Override
    @Transactional
    public List<RecommendationViewDto> getByFilters(RecommendationFilterDto filtersDto) {
        var recommendations = recommendationRepository.findAll().stream();
        for (RecommendationFilter filter : filters) {
            if (filter.isApplicable(filtersDto)) {
                recommendations = filter.filter(recommendations, filtersDto);
            }
        }
        return recommendations
                .map(recommendationMapper::toViewDto)
                .toList();
    }

    private void validateAuthorIsNotReceiver(Long authorId, Long receiverId) {
        if (authorId.equals(receiverId)) {
            throw new DataValidationException("Вы не можете написать рекомендацию себе!");
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
}
