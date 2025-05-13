package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {

    @Mapping(target = "skill", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    @Mapping(target = "skillId", source = "skill", expression = "java(skillOffer.getSkill().getId()")
    @Mapping(target = "recommendationId", source = "recommendation", expression = "java(skillOffer.getRecommendation().getId()")
    SkillOfferDto toDto(SkillOffer skillOffer);
}
