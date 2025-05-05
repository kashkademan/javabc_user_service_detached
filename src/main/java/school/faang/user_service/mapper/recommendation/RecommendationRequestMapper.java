package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "requesterId", ignore = true)
    @Mapping(target = "receiverId", ignore = true)
    RecommendationRequestDto toDto(RecommendationRequest entity);

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto);
}
