package school.faang.user_service.filter.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Slf4j
@Component
public class RecommendationRequestFilterByStatus implements Filter<RequestFilterDto, RecommendationRequest> {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.status() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> recommendationRequest,
            RequestFilterDto filterDto
    ) {
        return recommendationRequest
                .filter(request -> request.getStatus().equals(filterDto.status()));
    }
}
