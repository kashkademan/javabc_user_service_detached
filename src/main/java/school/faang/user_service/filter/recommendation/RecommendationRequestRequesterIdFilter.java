package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

/**
 * Фильтр для получения запроса рекомендации по индектификатору отправителя (requester)
 * <p>
 * Фильтр отбирает запросы рекомендации по ID отправителя ({@code requesterId})
 * Фильтр применяется, когда значения поля DTO {@code requesterId} не null
 * </p>*
 *
 * @author mazin
 * @since 08.07.2025
 */
@Component
public class RecommendationRequestRequesterIdFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto dto) {
        return dto.requesterId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> requests,
            RecommendationRequestFilterDto filterDto) {
        return requests
                .filter(request -> request.getRequester().getId().equals(filterDto.requesterId()));
    }
}