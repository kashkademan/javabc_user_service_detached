package school.faang.user_service.dto.recommendation;

import lombok.Builder;

@Builder
public record RequestFilterDto(
        Long requesterId,
        Long receiverId,
        String messagePattern,
        String status) {
}
