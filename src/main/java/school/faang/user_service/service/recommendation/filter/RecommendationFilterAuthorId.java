package school.faang.user_service.service.recommendation.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.controller.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.stream.Stream;

@Component
public class RecommendationFilterAuthorId implements RecommendationFilter {
    @Override
    public boolean isApplicable(RecommendationFilterDto filter) {
        return filter.getAuthorId() != null;
    }

    @Override
    public Stream<Recommendation> filter(Stream<Recommendation> recommendations,
                                         RecommendationFilterDto recommendationFilterDto) {
        return recommendations.filter(recommendation ->
                recommendation.getAuthor().getId().equals(recommendationFilterDto.getAuthorId()));
    }
}
