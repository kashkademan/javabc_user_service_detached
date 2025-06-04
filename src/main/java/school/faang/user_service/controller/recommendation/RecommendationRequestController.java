package school.faang.user_service.controller.recommendation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationRejectDto;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationResponseDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendation-requests")
@Validated
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationResponseDto create(
            @Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping
    public List<RecommendationResponseDto> getFiltered(
            @Valid RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationResponseDto getById(@PathVariable @Min(value = 1, message = "id must be a positive number") long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PostMapping("/{id}/reject")
    public RecommendationResponseDto reject(
            @PathVariable @Min(value = 1, message = "id must be a positive number") long id,
            @Valid @RequestBody RecommendationRejectDto rejectDto) {
        return recommendationRequestService.rejectRequest(id, rejectDto);
    }

    @ExceptionHandler({EntityNotFoundException.class, DataValidationException.class})
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}