package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;

    @Override
    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        long authorId = userContext.getUserId();
        Long receiverId = recommendationDto.receiverId();
        validateRecommendationCreationRights(authorId, receiverId);
        log.info("Creating new recommendation");
        Long newRecommendationId = recommendationRepository.create(authorId, receiverId, recommendationDto.content());
        Recommendation recommendation = recommendationRepository.findById(newRecommendationId)
                .orElseThrow(() -> new DataValidationException("Recommendation has not been created"));
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    public RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        Recommendation recommendation = validateRecommendationEditingRights(recommendationId);
        log.info("Updating recommendation");
        recommendationMapper.update(recommendationDto, recommendation);
        recommendationRepository.save(recommendation);
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    public void delete(long recommendationId) {
        Recommendation recommendation = validateRecommendationEditingRights(recommendationId);
        log.info("Deleting recommendation");
        recommendationRepository.deleteByIdAndAuthor_id(recommendationId, recommendation.getAuthor().getId());
    }

    @Override
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        log.info("Performing recommendation search using provided filter(s)");
        return recommendationRepository.findAll().stream()
                .filter(rec -> rec.getAuthor().getId().equals(filters.authorId()))
                .filter(rec -> rec.getReceiver().getId().equals(filters.receiverId()))
                .filter(rec -> rec.getContent().contains(filters.contentContains()))
                .map(recommendationMapper::toRecommendationDto).toList();
    }

    private void validateRecommendationCreationRights(Long authorId, Long receiverId) {
        if (authorId.equals(receiverId)) {
            throw new ForbiddenException("Leaving a recommendation to self is not allowed!");
        }
        Recommendation latestRecommendationByAuthorForReceiver = recommendationRepository
                .findAllByAuthorId(authorId).stream()
                .filter(recommendation -> recommendation.getReceiver().getId().equals(receiverId))
                .max(Comparator.comparing(Recommendation::getCreatedAt)).orElse(null);
        LocalDateTime latestPreviousRecommendationCreatedAtThreshold = LocalDateTime.now().minusMonths(6);
        if (latestRecommendationByAuthorForReceiver != null) {
            if (latestRecommendationByAuthorForReceiver.getCreatedAt()
                    .isAfter(latestPreviousRecommendationCreatedAtThreshold)
            ) {
                throw new ForbiddenException("Last recommendation for this user was created later than 6 months ago");
            }
        }
    }

    private Recommendation validateRecommendationEditingRights(long recommendationId) {
        Long userId = userContext.getUserId();
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataValidationException("Recommendation with this ID does not exist!"));
        if (!recommendation.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Author ID mismatch,"
                    + " editing this recommendation is not allowed for this user!");
        }
        return recommendation;
    }
}