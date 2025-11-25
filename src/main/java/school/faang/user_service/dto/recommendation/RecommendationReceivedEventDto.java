package school.faang.user_service.dto.recommendation;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RecommendationReceivedEventDto(
        long id,
        long authorId,
        long receiverId,
        LocalDateTime createdAt
) {

}
