package school.faang.user_service.dto.recommendation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRecommendationRequestDto(
        @Schema(description = "ID of the recipient of the recommendation request")
        @NotNull
        Long receiverId,

        @Schema(description = "Message for the recommendation request")
        @NotNull
        @NotBlank
        String message
) {
}
