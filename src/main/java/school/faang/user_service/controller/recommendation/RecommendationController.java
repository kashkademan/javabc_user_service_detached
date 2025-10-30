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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MessageDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Tag(name = "recommendation (v1)", description = "Controller for recommendations")
@RestController
@RequestMapping(value = "api/v1/recommendations")
@RequiredArgsConstructor
@Validated
public class RecommendationController {
    private final RecommendationService recommendationService;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation"),
            @ApiResponse(responseCode = "400", description = "Server error due to invalid data", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Operation(summary = "Create a recommendation")
    @PostMapping
    public RecommendationDto createRecommendation(@RequestBody @Valid CreateRecommendationDto dto) {
        return recommendationService.create(dto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful update"),
            @ApiResponse(responseCode = "400", description = "Server error due to invalid data", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Recommendation not found", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Operation(summary = "Update a recommendation")
    @PutMapping()
    public RecommendationDto updateRecommendation(@RequestBody @Valid UpdateRecommendationDto dto) {
        return recommendationService.update(dto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful delete"),
            @ApiResponse(responseCode = "400", description = "Server error due to invalid data", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Recommendation not found", content =
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @Operation(summary = "Delete a recommendation")
    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable @Positive Long id) {
        recommendationService.delete(id);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Server error due to invalid data", content =
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageDto.class)))
    })
    @Operation(summary = "Receiving recommendations")
    @GetMapping
    List<RecommendationDto> getByFilters(@RequestParam RecommendationFilterDto filters) {
        return recommendationService.getByFilters(filters);
    }
}
