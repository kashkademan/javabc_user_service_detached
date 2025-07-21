package school.faang.user_service.dto.recommendation;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RecommendationDto(Long id, Long authorId, Long receiverId,
                                String content, LocalDateTime createdAt) {
}
