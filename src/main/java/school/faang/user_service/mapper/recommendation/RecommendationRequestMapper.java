package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Mapper(componentModel = "spring", imports = {User.class, RequestStatus.class})
public interface RecommendationRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RecommendationRequest toRecommendationRequest(CreateRecommendationRequestDto dto);

    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);

    List<RecommendationRequestDto> toRecommendationRequestDtoList(List<RecommendationRequest> recommendationRequests);
}