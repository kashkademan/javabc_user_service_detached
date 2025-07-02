package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.CreateRecommendationRequestDto;
import school.faang.user_service.dto.user.RecommendationRequestDto;
import school.faang.user_service.dto.user.RecommendationRequestFilterDto;
import school.faang.user_service.dto.user.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto create(CreateRecommendationRequestDto recommendationDto) {
        validateString(recommendationDto.message(), "message");
        validateNotNull(recommendationDto.receiverId(), "receiverId");
        return recommendationRequestService.create(recommendationDto);
    }

    public List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filters) {
        validateNotNull(filters.receiverId(), "receiverId");
        validateNotNull(filters.requesterId(), "requesterId");
        return recommendationRequestService.getByFilters(filters);
    }

    public RecommendationRequestDto getById(long id) {
        return recommendationRequestService.getById(id);
    }

    public void accept(long id) {
        recommendationRequestService.accept(id);
    }

    void reject(long id, RejectionDto rejection) {
        validateString(rejection.reason(), "reason");
        recommendationRequestService.reject(id, rejection);
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isNotBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}
