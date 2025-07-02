package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper
public interface RecommendationRequestMapper {
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RecommendationRequest toRecommendationRequest(CreateRecommendationRequestDto dto);

    @Mapping(target = "receiver", source = "receiver")
    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);

}
