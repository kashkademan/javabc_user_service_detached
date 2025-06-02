package school.faang.user_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RequestFilterDto(
        Long requesterId,
        Long receiverId,
        Long recommendationId,
        String messagePattern,
        LocalDateTime createdAfter,
        LocalDateTime createdBefore
) {
}