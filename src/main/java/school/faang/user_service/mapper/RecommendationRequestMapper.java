package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(CreateRecommendationRequestDto dto);

    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkillIds")
    RecommendationRequestDto toDto(RecommendationRequest request);

    @Named("mapSkillIds")
    default List<Long> mapSkillIds(List<SkillRequest> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills.stream()
                .map(skill -> skill.getSkill().getId())
                .toList();
    }
}
