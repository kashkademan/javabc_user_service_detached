package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record RecommendationReceivedEvent(
        @NotNull(message = "Recommendation ID cannot be null")
        @Positive
        Long recommendationId,
        @NotNull(message = "Author recommendation ID cannot be null")
        @Positive
        Long authorId,
        @NotNull(message = "Receiver recommendation ID cannot be null")
        @Positive
        Long receiverId,
        LocalDateTime recommendationDate
) {
}