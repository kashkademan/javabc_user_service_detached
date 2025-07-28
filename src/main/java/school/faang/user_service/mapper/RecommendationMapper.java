package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    RecommendationViewDto toViewDto(Recommendation recommendation);
}
