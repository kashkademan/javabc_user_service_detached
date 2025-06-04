package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequiredArgsConstructor
@Validated
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Operation(
            summary = "Создание рекомендации",
            description = "Создает новую рекомендацию с возможным списком предложенных навыков."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Рекомендация успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecommendationDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/recommendation/create")
    public RecommendationDto giveRecommendation(@RequestBody @Valid RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @Operation(
            summary = "Обновление рекомендации",
            description = "Вносит изменения в уже существующую рекомендацию."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Рекомендация успешно обновлена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecommendationDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/recommendation/update")
    public RecommendationDto updateRecommendation(@RequestBody @Valid RecommendationDto updated) {
        return recommendationService.update(updated);
    }

    @Operation(
            summary = "Удаление рекомендации",
            description = "Удаляет рекомендацию полностью"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Рекомендация успешно удалена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecommendationDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/recommendation/{recommendationId}/delete")
    public boolean deleteRecommendation(@PathVariable @Min(1) long recommendationId) {
        return recommendationService.delete(recommendationId);
    }

    @Operation(
            summary = "Рекомендаций оставленные пользователю",
            description = "Вернет в виде страниц список рекомендаций пользователя."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Успешное выполнение!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecommendationDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/recommendation/{receiverId}/getAllUserRecommendations")
    public Page<RecommendationDto> getAllUserRecommendations(
            @PathVariable @Min(1) long receiverId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(5) int size
    ) {
        return recommendationService.getAllUserRecommendations(receiverId, page, size);
    }

    @Operation(
            summary = "Рекомендации от пользователя",
            description = "Вернет список рекомендаций которые выдал пользователь"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Рекомендация успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecommendationDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/recommendation/{authorId}/getAllGivenRecommendations")
    public Page<RecommendationDto> getAllGivenRecommendations(
            @PathVariable @Min(1) long authorId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(5) int size) {

        return recommendationService.getAllGivenRecommendations(authorId, page, size);
    }
}