package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.CreateRecommendationRequestDto;
import school.faang.user_service.dto.user.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper
public interface RecommendationRequestMapper {
    @Mapping(target = "receiver", source = "dto.receiverId")
    @Mapping(target = "message", source = "dto.receiverId")
    RecommendationRequest toRecommendationRequest(CreateRecommendationRequestDto dto);

    @Mapping(target = "message", source = "entity.receiver")
    @Mapping(target = "receiverId", source = "entity.id")
    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);
}
