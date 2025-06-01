package school.faang.user_service.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostPublishEvent(
        Long postId,
        Long authorId,
        LocalDateTime publishedAt
) {}
