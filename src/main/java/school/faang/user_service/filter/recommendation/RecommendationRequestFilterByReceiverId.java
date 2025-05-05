package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class RecommendationRequestFilterByReceiverId implements Filter<RequestFilterDto, RecommendationRequest> {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.receiverId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> recommendationRequest,
            RequestFilterDto filterDto
    ) {
        return recommendationRequest
                .filter(request -> request.getReceiver().getId().equals(filterDto.receiverId()));
    }
}
