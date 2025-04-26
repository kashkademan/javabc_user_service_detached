package school.faang.user_service.dto.event;

public record FollowEventDto(
        long followeeId,
        long followerId
) {
}
