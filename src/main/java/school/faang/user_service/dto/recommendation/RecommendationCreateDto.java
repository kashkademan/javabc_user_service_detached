package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecommendationCreateDto(
        @NotNull
        Long authorId,
        @NotNull
        Long receiverId,
        @NotBlank
        String content) {

}
