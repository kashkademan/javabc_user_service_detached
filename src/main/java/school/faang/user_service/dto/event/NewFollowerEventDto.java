package school.faang.user_service.dto.event;

public record NewFollowerEventDto(
        long actorId,
        long receiverId,
        String followerDisplayName
) {
}

