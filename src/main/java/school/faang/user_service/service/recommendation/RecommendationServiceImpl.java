package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.recommendation.RecommendationController;
import school.faang.user_service.controller.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;
    private final UserMapper userMapper;
    private final List<RecommendationFilter> filters;

    @Override

    public RecommendationDto create(CreateRecommendationDto createRecommendationDto) {
        LocalDateTime sixMonthAgo = LocalDateTime.now().minusMonths(6);

        Long authorId = userContext.getUserId();
        Long receiverId = createRecommendationDto.receiverId();

        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        if (authorId.equals(receiverId)) {
            throw new DataValidationException("Вы не можете написать рекомендацию себе");
        }

        if (lastRecommendation.isPresent() &&
                lastRecommendation.get().getCreatedAt().isAfter(sixMonthAgo)) {
            throw new ForbiddenException("Вы не можете писать рекомендации чаще, чем раз в полгода");
        }

        Long recommendationId = recommendationRepository.create(
                authorId,
                receiverId,
                createRecommendationDto.content()
        );

        Recommendation savedRecommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Не удалось найти созданную рекомендацию"));
        log.info("Рекомендация для пользоватеоля {} создана", createRecommendationDto.receiverId());
        return recommendationMapper.toRecommedationDto(savedRecommendation);
    }

    @Override
    public RecommendationDto update(long recommendationId, UpdateRecommendationDto updateRecommendationDto) {
        Long currentId = userContext.getUserId();
        Recommendation recommendation = recommendationRepository.findById(updateRecommendationDto.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Рекомендация не найдена"));

        if (!currentId.equals(updateRecommendationDto.getAuthorId())) {
            throw new ForbiddenException("Вы можете редактировать только свои рекомендации");
        }

        recommendation.setContent(updateRecommendationDto.getContent());

        Recommendation updatedRecommendation = recommendationRepository.update(updateRecommendationDto.getAuthorId(),
                updateRecommendationDto.getRecieverId(), updateRecommendationDto.getContent());

        log.info("Рекомендация для пользователя {} обновлена", updateRecommendationDto.getRecieverId());
        return recommendationMapper.toRecommedationDto(updatedRecommendation);
    }

    @Override
    public void delete(long recommendationId) {
        log.info("Рекомендация {} удалена", recommendationId);
        recommendationRepository.deleteByIdAndAuthor_id(recommendationId, userContext.getUserId());
    }

    @Override
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filtersDto) {
        var recommendations = recommendationRepository.findAll().stream();
        for(RecommendationFilter filter : filters) {
            if(filter.isApplicable(filtersDto)) {
                recommendations = filter.filter(recommendations, filtersDto);
            }
        }
        return recommendations
                .map(recommendationMapper::toRecommedationDto)
                .toList();
    }
}
