package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RequestMapping(value = "/request")
@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping(value = "/create")
    public RecommendationRequestDto create(@RequestBody CreateRecommendationRequestDto recommendationDto) {
        validateString(recommendationDto.message(), "message");
        validateNotNull(recommendationDto.receiverId(), "receiverId");
        return recommendationRequestService.create(recommendationDto);
    }

    @PostMapping(value = "/getf")
    public List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filters) {
        validateNotNull(filters.receiverId(), "receiverId");
        validateNotNull(filters.requesterId(), "requesterId");
        return recommendationRequestService.getByFilters(filters);
    }

    @PostMapping(value = "/getid")
    public RecommendationRequestDto getById(long id) {
        return recommendationRequestService.getById(id);
    }

    public void accept(long id) {
        recommendationRequestService.accept(id);
    }

    public void reject(long id, RejectionDto rejection) {
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
