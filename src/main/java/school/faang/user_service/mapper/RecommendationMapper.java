package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Recommendation toEntity(RecommendationDto recommendationDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOffers", expression = "java(toSkillOfferDtoList(recommendation))")
    RecommendationDto toDto(Recommendation recommendation);

    default List<SkillOfferDto> toSkillOfferDtoList(Recommendation recommendation) {
        return recommendation.getSkillOffers().stream()
                .map(skillOffer ->
                        new SkillOfferDto(skillOffer.getId(),
                                skillOffer.getSkill().getId(),
                                skillOffer.getRecommendation().getId()))
                .toList();
    }
}
