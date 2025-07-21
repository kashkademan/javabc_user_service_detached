package school.faang.user_service.filter;

import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.stream.Stream;

public class RecommendationReceiverFilterInstance implements RecommendationFilter{
    @Override
    public boolean isApplicable(RecommendationFilterDto filter) {
        return true;
    }

    @Override
    public Stream<Recommendation> apply(Stream<Recommendation> recommendations, RecommendationFilterDto filter) {
        return recommendations
                .filter(r -> r.getReceiver().getId().equals(3L));
    }
}
