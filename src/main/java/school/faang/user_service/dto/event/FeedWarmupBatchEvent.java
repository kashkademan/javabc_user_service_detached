package school.faang.user_service.dto.event;

public record FeedWarmupBatchEvent(
        int page,
        int size
) {}
