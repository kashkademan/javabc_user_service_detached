package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;


import java.util.List;

/**
 * Сервис для управления рекомендациями
 */
public interface RecommendationService {
    RecommendationViewDto create(RecommendationCreateDto recommendationDto);

    RecommendationViewDto update(long recommendationId, RecommendationUpdateDto recommendationDto);

    void delete(long recommendationId);

    List<RecommendationViewDto> getByFilters(RecommendationFilterDto filters);
}
