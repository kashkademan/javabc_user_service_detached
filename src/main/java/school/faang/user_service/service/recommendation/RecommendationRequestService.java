package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;

import java.util.List;


public interface RecommendationRequestService {

    RecommendationRequestDto create(CreateRecommendationRequestDto createRecommendationRequestDto);

    RecommendationRequestDto getById(Long id);

    List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filterDto);

    void accept(Long id);

    void reject(Long id, RejectionDto dto);
}