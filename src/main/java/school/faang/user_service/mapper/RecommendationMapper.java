package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

/**
 * MapStruct Recommendation -> RecommendationDto.
 */
@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    RecommendationDto toRecommendationDto(Recommendation recommendation);
}
