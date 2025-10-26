package school.faang.user_service.dto.recommendation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateRecommendationDto(
        @Schema(description = "ID of the recipient of the recommendation")
        @NotNull
        Long receiverId,

        @Schema(description = "description of recommendation")
        @NotNull
        String content
)
{}
