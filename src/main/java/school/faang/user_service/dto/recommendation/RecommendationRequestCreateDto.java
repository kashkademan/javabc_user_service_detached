package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.List;

import static school.faang.user_service.dto.recommendation.RecommendationRequestConstraints.MAX_SIZE_STRING;
import static school.faang.user_service.dto.recommendation.RecommendationRequestConstraints.MESSAGE_SIZE_INVALID;

public record RecommendationRequestCreateDto(
        @NotBlank
        @Size(max = MAX_SIZE_STRING, message = MESSAGE_SIZE_INVALID)
        String message,
        @NonNull
        Long receiverId,
        List<Long> skillIds
) {

}
