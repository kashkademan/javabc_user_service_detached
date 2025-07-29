package school.faang.user_service.filter;

import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.stream.Stream;

public interface RecommendationFilter {
    boolean isApplicable(RecommendationFilterDto filter);

    Stream<Recommendation> apply(Stream<Recommendation> recommendations,
                                 RecommendationFilterDto filter);
}
