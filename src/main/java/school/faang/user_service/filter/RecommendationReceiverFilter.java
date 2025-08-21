package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class RecommendationReceiverFilter implements RecommendationFilter {
    @Override
    public boolean isApplicable(RecommendationFilterDto filter) {
        return filter.receiverId() != null;
    }

    @Override
    public Stream<Recommendation> apply(Stream<Recommendation> recommendations, RecommendationFilterDto filter) {
        return recommendations
                .filter(r -> Objects.equals(r.getReceiver().getId(), filter.receiverId()));
    }

}
