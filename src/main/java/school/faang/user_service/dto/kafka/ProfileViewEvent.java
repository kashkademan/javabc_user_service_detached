package school.faang.user_service.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileViewEvent(
        @JsonProperty("viewerId") Long viewerId,
        @JsonProperty("profileOwnerId") Long profileOwnerId
) {
}
