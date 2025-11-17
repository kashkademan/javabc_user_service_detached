package school.faang.user_service.dto.recommendation;

import java.time.LocalDateTime;

public record RecommendationReceivedEventDto(
        long id,
        long authorId,
        long receiverId,
        LocalDateTime createdAt
) {

}
