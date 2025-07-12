package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    RecommendationRequest toEntity(CreateRecommendationRequestDto dto);

    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);
}
