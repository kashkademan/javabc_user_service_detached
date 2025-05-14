package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {

    @Mapping(source = "skill.id", target = "skillId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    List<SkillOfferDto> toDtoList(List<SkillOffer> skillOffers);

    @Mapping(target = "skill", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    List<SkillOffer> toEntityList(List<SkillOfferDto> skillOfferDtos);
}