package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Objects;

//TODO: 6 months last recommendation check - method, add to create()
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;

    @Override
    public RecommendationDto create(CreateRecommendationDto createRecommendationDto) {
        if (!createRecommendationDto.receiverId().equals(userContext.getUserId())) {
            long authorId = userContext.getUserId();
            long receiverId = createRecommendationDto.receiverId();
            String content = createRecommendationDto.content();
            long newRecommendationId = recommendationRepository.create(authorId,
                    receiverId, content);
            return recommendationMapper.toRecommendationDto(recommendationRepository
                    .findById(newRecommendationId).orElseThrow(EntityNotFoundException::new));
        } else {
            throw new DataValidationException("Self recommending is forbidden, but nice try...");
        }
    }

    @Override
    public RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        Recommendation recommendationToBeUpdated = recommendationRepository.findById(recommendationId)
                .orElseThrow(EntityNotFoundException::new);
        if (recommendationToBeUpdated.getAuthor().getId() == userContext.getUserId()) {
            recommendationRepository.save(recommendationToBeUpdated);
        } else {
            throw new ForbiddenException("Can't update recommendations authored by other users");
        }
        return recommendationMapper.toRecommendationDto(recommendationToBeUpdated);
    }

    @Override
    public void delete(long recommendationId) {
        Recommendation recommendationToBeDeleted = recommendationRepository.findById(recommendationId)
                .orElseThrow(EntityNotFoundException::new);
        if (recommendationToBeDeleted.getAuthor().getId() == userContext.getUserId()) {
            recommendationRepository.deleteByIdAndAuthor_id(recommendationId,
                    recommendationToBeDeleted.getAuthor().getId());
        } else {
            throw new ForbiddenException("Can't delete recommendations authored by other users");
        }
    }

    @Override
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        return recommendationRepository.findAll().stream()
                .filter(s -> s.getContent().contains(filters.contentContains()))
                .filter(s -> Objects.equals(s.getAuthor().getId(), filters.authorId()))
                .filter(s -> s.getReceiver().getId().equals(filters.receiverId()))
                .map(recommendationMapper::toRecommendationDto)
                .toList();
    }
}
