package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

/**
 * Фильтр запросов рекомендация по индентификатору получающего
 * <p>
 * Фильтр отбирает запросы рекомендаций по ID получателя (receiver).
 * Применяется, когда в DTO поле {@code receiverId} не null.
 * </p>*
 *
 * @author Linempy
 * @since 08.07.2025
 */
@Component
public class RecommendationRequestReceiverIdFilter implements RecommendationRequestFilter   {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto dto) {
        return dto.receiverId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> requests,
            RecommendationRequestFilterDto filterDto) {
        return requests
                .filter(request -> request.getReceiver().getId().equals(filterDto.receiverId()));
    }
}