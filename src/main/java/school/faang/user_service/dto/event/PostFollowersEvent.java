package school.faang.user_service.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostFollowersEvent(
        Long postId,
        Long authorId,
        List<Long> followerIds,
        LocalDateTime publishedAt
) {}
