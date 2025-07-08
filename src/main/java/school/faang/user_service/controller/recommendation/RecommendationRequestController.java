package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor

@RequestMapping("/recommendations")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public RecommendationRequestDto create(
            @Valid @RequestBody CreateRecommendationRequestDto recommendationDto) {
        return recommendationRequestService.create(recommendationDto);
    }

    @GetMapping("/filter")
    public List<RecommendationRequestDto> getByFilters(@Valid RecommendationRequestFilterDto filters) {
        return recommendationRequestService.getByFilters(filters);
    }

    @GetMapping("/{requestId}")
    public RecommendationRequestDto getById(@PathVariable long id) {
        return recommendationRequestService.getById(id);
    }

    @PostMapping("/{requestId}/accept")
    public void accept(@PathVariable long id) {
        recommendationRequestService.accept(id);
    }

    @PostMapping("/{requestId}/reject")
    public void reject(@PathVariable long id, @Valid RejectionDto rejectionDto) {
        recommendationRequestService.reject(id, rejectionDto);
    }

    private boolean isInvalidId(Long id) {
        return id == null;
    }

}

