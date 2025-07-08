package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

/**
 * Фильтр для запросов рекомендации по статусу
 * <p>
 * Фильтр отбирает запросы рекомендации, статус которых соответствует указанному  значению.
 * Применяется, когда значение поля DTO {@code status} не null
 * </p>*
 *
 * @author mazin
 * @since 08.07.2025
 */
@Component
public class RecommendationRequestStatusFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto dto) {
        return dto.status() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> requests,
            RecommendationRequestFilterDto filterDto) {
        return requests
                .filter(request -> request.getStatus().equals(filterDto.status()));
    }
}