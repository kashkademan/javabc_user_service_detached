package school.faang.user_service.mapper.Recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    default List<SkillOfferDto> toDtoList(List<SkillOffer> skillOffers) {
        return skillOffers.stream().map(this::toDto).collect(Collectors.toList());
    }
}
