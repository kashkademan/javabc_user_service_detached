package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

/**
 * Интерфейс для фильтрации запросов рекомендации
 * <p>
 * Определяет контракт для реализации различных фильтров запросов рекомендации.
 * </p>
 * @author mazin
 * @since 08.07.2025
 * */
public interface RecommendationRequestFilter {
    boolean isApplicable(RecommendationRequestFilterDto dto);

    Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> requests,
            RecommendationRequestFilterDto filterDto);
}