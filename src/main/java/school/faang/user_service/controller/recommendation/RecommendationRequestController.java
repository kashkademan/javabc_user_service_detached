package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MessageDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Tag(name = "recommendation-request (v1)", description = "Controller for recommendation requests")
@RestController
@RequestMapping(value = "api/v1/recommendation-requests")
@RequiredArgsConstructor
@Validated
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation"),
            @ApiResponse(responseCode = "400", description = "Server error due to invalid data", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Operation(summary = "Create a recommendation request")
    @PostMapping
    public RecommendationRequestDto createRecommendationRequest(@RequestBody @Valid CreateRecommendationRequestDto dto) {
        return recommendationRequestService.create(dto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Server error due to invalid data", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class)))
    })
    @Operation(summary = "Get recommendation requests by filters")
    @GetMapping
    public List<RecommendationRequestDto> getByFilters(@Valid RecommendationRequestFilterDto filters) {
        return recommendationRequestService.getByFilters(filters);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "404", description = "Recommendation request not found", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Operation(summary = "Get recommendation request by id")
    @GetMapping("/{id}")
    public RecommendationRequestDto getById(@PathVariable @Positive Long id) {
        return recommendationRequestService.getById(id);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful acceptance"),
            @ApiResponse(responseCode = "400", description = "Server error due to invalid data", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Recommendation request not found", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Operation(summary = "Accept a recommendation request")
    @PutMapping("/{id}/accept")
    public void accept(@PathVariable @Positive Long id) {
        recommendationRequestService.accept(id);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful rejection"),
            @ApiResponse(responseCode = "400", description = "Server error due to invalid data", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Recommendation request not found", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Operation(summary = "Reject a recommendation request")
    @PutMapping("/{id}/reject")
    public void reject(@PathVariable @Positive Long id, @RequestBody @Valid RejectionDto rejection) {
        recommendationRequestService.reject(id, rejection);
    }
}
