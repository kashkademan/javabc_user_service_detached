package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record NewFollowerEventDto(
        long actorId,
        long receiverId,
        String followerDisplayName
) {
    /**
     * Computed Kafka message key: actorId-receiverId-eventType.
     * Not part of the JSON payload.
     */
    @JsonIgnore
    public String getKey() {
        return Long.toString(receiverId);
    }
}

