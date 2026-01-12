package school.faang.user_service.event;

import java.time.LocalDateTime;

public record FollowerEvent(
        long followeeId,
        long followerId,
        LocalDateTime timestamp
) {}
