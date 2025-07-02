package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserSubscriptionController {
    private final UserSubscriptionService subscriptionService;
    private final UserContext userContext;

    public void followUser(long followeeId) {
        Long followerId = userContext.getUserId();
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followeeId) {
        Long followerId = userContext.getUserId();
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public CountResponse getFollowersCount(long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    public CountResponse getFolloweesCount(long followerId) {
        return subscriptionService.getFolloweesCount(followerId);
    }

    public List<UserDto> getFollowers(long followeeId) {
        return subscriptionService.getFollowers(followeeId);
    }

    public List<UserDto> getFollowees(long followerId) {
        return subscriptionService.getFollowees(followerId);
    }
}
