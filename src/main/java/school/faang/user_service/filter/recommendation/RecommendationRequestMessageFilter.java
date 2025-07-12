package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

/**
 * RecommendationRequestMessageFilter — фильтр по содержанию текста в сообщении
 * <p>
 * Фильтр проверяет наличие указанной подстроки в тексте сообщения запроса рекомендации.
 * Поиск выполняется без учета регистра. Фильтр применяется тогда и только тогда, когда
 * не пусто значение поля {@code messageContains} у переданного DTO.
 * </p>*
 *
 * @author Linempy
 * @since 08.07.2025
 */
@Component
public class RecommendationRequestMessageFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto dto) {
        return !dto.messageContains().isBlank();
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> requests,
            RecommendationRequestFilterDto filterDto) {
        return requests
                .filter(request -> request.getMessage().toLowerCase()
                        .contains(filterDto.messageContains().toLowerCase()));
    }
}