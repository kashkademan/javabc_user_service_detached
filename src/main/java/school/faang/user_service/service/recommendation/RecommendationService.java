package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;

import java.util.List;

public interface RecommendationService {

    RecommendationDto create(CreateRecommendationDto recommendationDto);

    RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto);

    void delete(long recommendationId);

    List<RecommendationDto> getByFilters(RecommendationFilterDto filters);
}
