package school.faang.user_service.filter.recommendation.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterStrategy;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SkillIdFilter implements RecommendationRequestFilterStrategy {

    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getSkillId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RequestFilterDto requestFilterDto) {
        return recommendationRequests
                .filter(recommendationRequest ->
                        recommendationRequest.getSkills().stream()
                                .anyMatch(skill -> skill.getSkill().getId() == requestFilterDto.getSkillId()));
    }
}
