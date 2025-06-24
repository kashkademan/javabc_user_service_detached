package school.faang.user_service.config.kafka.dto;

import lombok.Builder;
import school.faang.user_service.config.kafka.enums.SubscriptionEventType;
import school.faang.user_service.dto.user.UserDto;

@Builder
public record SubscriptionEvent(
        SubscriptionEventType subscriptionEventType,
        UserDto owner,
        UserDto follower
) {}