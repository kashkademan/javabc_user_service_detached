package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRecommendationDto(
        @NotBlank
        @Size(max = 4096, message = "Recommendation content cannot be longer than 4096 characters")
        String content
) {

}
