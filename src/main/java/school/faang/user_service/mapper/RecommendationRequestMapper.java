package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    RecommendationRequest toRecommendationRequest(CreateRecommendationRequestDto dto);

    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);
}
