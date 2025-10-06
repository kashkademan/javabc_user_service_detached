package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.config.context.UserContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Реализация RecommendationService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;
    private final SkillOfferRepository skillOfferRepository;

    @Value("${recommendation.cooldown.months:6}")
    private int cooldownMonths;

    @Override
    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        final Long authorId = userContext.getUserId();
        final Long receiverId = recommendationDto.receiverId();
        final String content = recommendationDto.content();

        log.info("Creating recommendation: authorId={}, receiverId={}", authorId, receiverId);

        if (receiverId == null) {
            throw new DataValidationException("receiverId is required");
        }
        if (isBlank(content)) {
            throw new DataValidationException("content must not be blank");
        }
        if (Objects.equals(authorId, receiverId)) {
            throw new ForbiddenException("You cannot create recommendation for yourself");
        }

        final LocalDateTime boundary = LocalDateTime.now().minusMonths(cooldownMonths);
        boolean violatesCooldown = recommendationRepository.findAll().stream()
                .filter(r -> Objects.equals(r.getAuthor().getId(), authorId))
                .filter(r -> Objects.equals(r.getReceiver().getId(), receiverId))
                .anyMatch(r -> {
                    LocalDateTime createdAt = r.getCreatedAt();
                    return createdAt != null && createdAt.isAfter(boundary);
                });

        if (violatesCooldown) {
            throw new DataValidationException(
                    "You can leave a recommendation for this user only once in " + cooldownMonths + " months");
        }

        Long newId = recommendationRepository.create(authorId, receiverId, content);
        log.debug("Recommendation created with id={}", newId);

        if (recommendationDto.skillIds() != null && !recommendationDto.skillIds().isEmpty()) {
            for (Long skillId : recommendationDto.skillIds()) {
                if (skillId != null) {
                    skillOfferRepository.create(skillId, newId);
                }
            }
        }

        Recommendation created = recommendationRepository.findById(newId)
                .orElseThrow(() -> new DataValidationException("Created recommendation not found by id=" + newId));

        return recommendationMapper.toRecommendationDto(created);
    }

    @Override
    public RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        final Long currentUserId = userContext.getUserId();
        log.info("Updating recommendation id={} by user={}", recommendationId, currentUserId);

        if (recommendationDto == null || isBlank(recommendationDto.content())) {
            throw new DataValidationException("content must not be blank");
        }

        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataValidationException("Recommendation not found: id=" + recommendationId));

        if (!Objects.equals(recommendation.getAuthor().getId(), currentUserId)) {
            throw new ForbiddenException("You can update only your own recommendation");
        }

        recommendation.setContent(recommendationDto.content());
        Recommendation saved = recommendationRepository.save(recommendation);

        if (recommendationDto.skillIds() != null) {
            skillOfferRepository.deleteAllByRecommendationId(recommendationId);
            for (Long skillId : recommendationDto.skillIds()) {
                if (skillId != null) {
                    skillOfferRepository.create(recommendationId, skillId);
                }
            }
        }

        return recommendationMapper.toRecommendationDto(saved);
    }

    @Override
    public void delete(long recommendationId) {
        final long currentUserId = userContext.getUserId();
        log.info("Deleting recommendation id={} by user={}", recommendationId, currentUserId);

        int affected = recommendationRepository.deleteByIdAndAuthor_id(recommendationId, currentUserId);
        if (affected == 0) {
            throw new ForbiddenException("You can delete only your own recommendation or it does not exist");
        }

        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
        log.debug("Recommendation id={} deleted", recommendationId);
    }

    @Override
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        log.info("Fetching recommendations by filters: {}", filters);

        String contentContains = filters != null ? filters.contentContains() : null;
        Long authorId = filters != null ? filters.authorId() : null;
        Long receiverId = filters != null ? filters.receiverId() : null;

        return recommendationRepository.findAll().stream()
                .filter(r -> authorId == null || Objects.equals(r.getAuthor().getId(), authorId))
                .filter(r -> receiverId == null || Objects.equals(r.getReceiver().getId(), receiverId))
                .filter(r -> {
                    if (isBlank(contentContains)) return true;
                    String c = r.getContent();
                    return c != null && c.toLowerCase().contains(contentContains.toLowerCase());
                })
                .map(recommendationMapper::toRecommendationDto)
                .collect(Collectors.toList());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

}
