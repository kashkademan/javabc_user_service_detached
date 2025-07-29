package school.faang.user_service.service.recommendation.filter;

import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.stream.Stream;

/**
 * RecommendationFilterContentContains — фильтр по содержанию рекомендации.
 * <p>
 * Фильтрует рекомендации, оставляя только те, в содержимом которых
 * присутствует указанная подстрока (без учета регистра).
 * </p>
 *
 * @author bozya
 * @since 23.07.2025
 */
public class RecommendationFilterContentContains implements RecommendationFilter {
    @Override
    public boolean isApplicable(RecommendationFilterDto filter) {
        return filter.contentContains() != null
                && !filter.contentContains().isBlank();
    }

    @Override
    public Stream<Recommendation> filter(Stream<Recommendation> recommendations,
                                         RecommendationFilterDto filterDto) {
        String filterStr = filterDto.contentContains().toLowerCase();
        return recommendations.filter(recommendation ->
                recommendation.getContent() != null
                        && recommendation.getContent().toLowerCase().contains(filterStr));
    }
}