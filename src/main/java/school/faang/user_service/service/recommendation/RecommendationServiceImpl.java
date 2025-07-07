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
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;
    private final int repeatRecommendationTimeLimit = 6;


    @Override
    public RecommendationDto create(CreateRecommendationDto newRecommendationDto) {
        if (!newRecommendationDto.receiverId().equals(userContext.getUserId())) {
            if (latestRecommendationCheck(newRecommendationDto)) {
                long authorId = userContext.getUserId();
                long receiverId = newRecommendationDto.receiverId();
                String content = newRecommendationDto.content();
                long newRecommendationId = recommendationRepository.create(authorId,
                        receiverId, content);
                return recommendationMapper.toRecommendationDto(recommendationRepository
                        .findById(newRecommendationId).orElseThrow(EntityNotFoundException::new));
            } else {
                throw new ForbiddenException("Latest recommendation limit");
            }
        } else {
            throw new ForbiddenException("Self recommending is forbidden, but nice try...");
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

    public boolean latestRecommendationCheck(CreateRecommendationDto newRecommendationDto) {
        long author = userContext.getUserId();
        long receiver = newRecommendationDto.receiverId();
        Recommendation latestRecommendation = recommendationRepository.findAll().stream()
                .filter(s -> s.getAuthor().getId().equals(author)
                        && s.getReceiver().getId().equals(receiver))
                .sorted(Comparator.comparing(Recommendation::getCreatedAt).reversed())
                .findFirst().orElseThrow();

        return ChronoUnit.MONTHS.between(latestRecommendation.getCreatedAt(),
                LocalDateTime.now()) > repeatRecommendationTimeLimit;
    }
}
