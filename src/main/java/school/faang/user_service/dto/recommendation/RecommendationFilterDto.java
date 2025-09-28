package school.faang.user_service.dto.recommendation;

import lombok.Builder;

@Builder
public record RecommendationFilterDto(String contentContains, Long authorId, Long receiverId) {
}
