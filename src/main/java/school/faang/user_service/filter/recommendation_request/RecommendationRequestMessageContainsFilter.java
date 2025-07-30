package school.faang.user_service.filter.recommendation_request;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestMessageContainsFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return StringUtils.isNotBlank(recommendationRequestFilterDto.messageContains());
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequestStream,
                                               RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return recommendationRequestStream.filter(recommendationRequest ->
                recommendationRequest.getMessage().toLowerCase()
                        .contains(recommendationRequestFilterDto.messageContains().toLowerCase()));
    }
}
