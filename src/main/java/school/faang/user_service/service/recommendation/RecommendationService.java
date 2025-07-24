package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;


import java.util.List;

/**
 * Сервис для управления рекомендациями
 */
public interface RecommendationService {
    RecommendationDto create(RecommendationCreateDto recommendationDto);

    RecommendationDto update(long recommendationId, RecommendationUpdateDto recommendationDto);

    void delete(long recommendationId);

    List<RecommendationDto> getByFilters(RecommendationFilterDto filters);
}
