package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {
    @Mapping(target = "skillId", source = "skill.id")
    @Mapping(target = "recommendationId", source = "recommendation.id")
    SkillOfferDto toDto(SkillOffer skillOffer);
}
