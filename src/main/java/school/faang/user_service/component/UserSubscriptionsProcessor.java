package school.faang.user_service.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FeedWarmupBatchEvent;
import school.faang.user_service.dto.event.UserSubscriptionsEvent;
import school.faang.user_service.publisher.FeedUsersResponsePublisher;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserSubscriptionsProcessor {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final FeedUsersResponsePublisher feedUsersResponsePublisher;

    public void processUserSubscriptions(FeedWarmupBatchEvent event) {
        List<Long> userIds = userService.getUserIdsByPage(event.page(), event.size());
        Map<Long, List<Long>> userSubscriptions = subscriptionService.getSubscriptionsIdsByUserIds(userIds);
        feedUsersResponsePublisher.publish(new UserSubscriptionsEvent(userSubscriptions));
    }
}
