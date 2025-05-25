package school.faang.user_service.dto.event;

import java.util.Objects;

public record FollowEventDto(long followeeId, long followerId) {

    @Override
    public String toString() {
        return "FollowEventDto[" +
                "followeeId=" + followeeId + ", " +
                "followerId=" + followerId + ']';
    }

}
