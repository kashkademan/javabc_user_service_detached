package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static school.faang.user_service.dto.recommendation.RecommendationRequestConstraints.MAX_SIZE_STRING;
import static school.faang.user_service.dto.recommendation.RecommendationRequestConstraints.MESSAGE_SIZE_INVALID;

public record RejectionDto(
        @NotBlank
        @Size(max = MAX_SIZE_STRING, message = MESSAGE_SIZE_INVALID)
        String reason
) {
}
