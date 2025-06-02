package school.faang.user_service.filter.recommendation;

import jakarta.validation.Valid;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RequesterIdFilter implements RecommendationFilter {
    @Override
    public boolean isApplicable(@Valid RequestFilterDto filters) {
        return true;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filters) {
        return requests.filter(request -> request.getRequester().getId().equals(filters.requesterId()));
    }
}