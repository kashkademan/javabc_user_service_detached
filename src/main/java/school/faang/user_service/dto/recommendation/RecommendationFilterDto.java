package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecommendationFilterDto(
        @NotBlank
        String contentContains,
        @NotNull
        Long authorId,
        @NotNull
        Long receiverId
) {}
