package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "recommendationRequest", ignore = true)
    Recommendation toEntity(RecommendationDto recommendationDto);

    @Mapping(target = "authorId", source = "author", expression = "java(recommendation.getAuthor().getId())")
    @Mapping(target = "receiverId", source = "receiver", expression = "java(recommendation.getReceiver().getId())")
    @Mapping(target = "skillOffers", source = "skillOffers", expression = "java(toSkillOfferDtoList(recommendation))")
    RecommendationDto toDto(Recommendation recommendation);
    default List<SkillOfferDto> toSkillOfferDtoList(Recommendation recommendation) {
        return recommendation.getSkillOffers().stream()
                .map(skillOffer -> {
                    SkillOfferMapper skillOfferMapper = new SkillOfferMapper() {
                        @Override
                        public SkillOffer toEntity(SkillOfferDto skillOfferDto) {
                            return null;
                        }

                        @Override
                        public SkillOfferDto toDto(SkillOffer skillOffer) {
                            return null;
                        }
                    };
                    return skillOfferMapper.toDto(skillOffer);
                }).toList();
    }
}
