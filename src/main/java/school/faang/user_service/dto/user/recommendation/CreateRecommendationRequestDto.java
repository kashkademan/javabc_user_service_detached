package school.faang.user_service.dto.user.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateRecommendationRequestDto(
        @NotNull
        @NotBlank
        String message,
        @NotNull
        @Positive
        Long requesterId,
        @NotNull
        @Positive
        Long receiverId) {
}