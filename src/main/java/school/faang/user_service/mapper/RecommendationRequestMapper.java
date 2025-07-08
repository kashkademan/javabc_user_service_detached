package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class},
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "id", ignore = true)
    RecommendationRequest toRecommendationRequest(CreateRecommendationRequestDto dto);

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);
}
