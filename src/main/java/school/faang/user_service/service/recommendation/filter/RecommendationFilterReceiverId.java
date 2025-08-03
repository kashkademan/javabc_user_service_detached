package school.faang.user_service.service.recommendation.filter;

import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.stream.Stream;

/**
 * RecommendationFilterReceiverId — фильтр рекомендаций по получателю.
 * <p>
 *
 * </p>
 *
 * @author bozya
 * @since 23.07.2025
 */
public class RecommendationFilterReceiverId implements RecommendationFilter {
    @Override
    public boolean isApplicable(RecommendationFilterDto filter) {
        return filter.receiverId() != null;
    }

    @Override
    public Stream<Recommendation> filter(Stream<Recommendation> recommendations,
                                         RecommendationFilterDto recommendationFilterDto) {
        Long receiverId = recommendationFilterDto.receiverId();
        return recommendations.filter(recommendation ->
                recommendation.getReceiver() != null
                        && recommendation.getReceiver().getId() != null
                        && recommendation.getReceiver().getId().equals(receiverId));
    }
}