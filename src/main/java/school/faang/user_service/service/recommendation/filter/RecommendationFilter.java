package school.faang.user_service.service.recommendation.filter;

import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.stream.Stream;

public interface RecommendationFilter {
    boolean isApplicable(RecommendationFilterDto filter);

    Stream<Recommendation> filter(Stream<Recommendation> recommendations,
                                  RecommendationFilterDto recommendationFilterDto);
}
