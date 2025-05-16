package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SkillOfferMapper.class})
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto toDto(Recommendation recommendation);

    List<RecommendationDto> toDtoList(List<Recommendation> recommendations);
}