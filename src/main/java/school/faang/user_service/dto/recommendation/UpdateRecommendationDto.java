package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRecommendationDto(
        @NotBlank(message = "Recommendation content must not be blank")
        @Size(max = 128, message = "Recommendation content must not exceed 128 characters")
        String content
) {}
