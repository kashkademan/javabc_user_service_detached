package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "receiverId", ignore = true)
    RecommendationDto toRecommendationDto(Recommendation recommendation);

}
