package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecommendationDto(
        @NotNull(message = "Recommendation receiver ID cannot be absent")
        Long id,
        @NotNull(message = "Recommendation author ID cannot be absent")
        Long authorId,
        @NotNull(message = "Recommendation receiver ID cannot be absent")
        Long receiverId,
        @NotBlank(message = "Recommendation content cannot be empty")
        @Size(max = 4096, message = "Recommendation content cannot be longer than 4096 characters")
        String content
) {
}
