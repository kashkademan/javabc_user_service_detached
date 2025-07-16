package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestViewDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestCreateDto dto);

    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkillIds")
    RecommendationRequestViewDto toViewDto(RecommendationRequest request);

    @Named("mapSkillIds")
    default List<Long> mapSkillIds(List<SkillRequest> skillRequests) {
        if (skillRequests == null) {
            return Collections.emptyList();
        }
        return skillRequests.stream()
                .map(skill -> skill.getSkill().getId())
                .toList();
    }
}
