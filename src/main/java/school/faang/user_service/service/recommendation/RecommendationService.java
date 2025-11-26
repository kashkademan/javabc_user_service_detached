package school.faang.user_service.service.recommendation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;

public interface RecommendationService {
    RecommendationDto create(CreateRecommendationDto recommendationDto);

    RecommendationDto update(UpdateRecommendationDto dto);

    void delete(Long recommendationId);

    Page<RecommendationDto> getByFilters(RecommendationFilterDto filters, Pageable pageable);
}
