package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record NewFollowerEventDto(
        long actorId,
        long receiverId,
        String followerDisplayName
) {
}

