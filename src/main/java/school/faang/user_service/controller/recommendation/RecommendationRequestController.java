package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation/requests")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public ResponseEntity<RecommendationRequestDto> requestRecommendation(
            @RequestBody @Valid CreateRecommendationRequestDto createRecommendationRequestDto) {
        RecommendationRequestDto response = recommendationRequestService.create(createRecommendationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> requestRecommendationById(@PathVariable Long id) {
        RecommendationRequestDto response = recommendationRequestService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RecommendationRequestDto>> getAllRequests(
            @ModelAttribute RecommendationRequestFilterDto filterDto) {
        if (filterDto.receiverId() == null && filterDto.requesterId() == null) {
            throw new DataValidationException("Either requesterId or receiverId must be provided");
        }

        List<RecommendationRequestDto> response = recommendationRequestService.getByFilters(filterDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptRequest(@PathVariable Long id) {
        recommendationRequestService.accept(id);
    }

    @PostMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable Long id, @RequestBody @Valid RejectionDto dto) {
        recommendationRequestService.reject(id, dto);
    }
}