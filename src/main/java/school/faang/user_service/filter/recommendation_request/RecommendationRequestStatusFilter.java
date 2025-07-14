package school.faang.user_service.filter.recommendation_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestStatusFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return recommendationRequestFilterDto.status() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequestStream,
                                               RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return recommendationRequestStream.filter(recommendationRequest ->
                recommendationRequestFilterDto.status().equals(recommendationRequest.getStatus()));
    }
}
