package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.user.CreateRecommendationRequestDto;
import school.faang.user_service.dto.user.RecommendationRequestDto;
import school.faang.user_service.dto.user.RecommendationRequestFilterDto;
import school.faang.user_service.dto.user.RejectionDto;

import java.util.List;

public interface RecommendationRequestService {

    RecommendationRequestDto create(CreateRecommendationRequestDto recommendationDto);

    List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filters);

    RecommendationRequestDto getById(long id);

    void accept(long id);

    void reject(long id, RejectionDto rejection);
}
