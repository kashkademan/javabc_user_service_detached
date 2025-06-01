package school.faang.user_service.dto.event;

import java.util.List;
import java.util.Map;

public record UserSubscriptionsEvent(
        Map<Long, List<Long>> userSubscriptions
) {}
