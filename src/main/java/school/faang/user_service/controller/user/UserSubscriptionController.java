package school.faang.user_service.controller.user;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    public void followUser(@NotNull long followeeId) {
        long userId = userContext.getUserId();
        userSubscriptionService.followUser(userId, followeeId);
    }

    public void unfollowUser(@NotNull long followeeId) {
        long userId = userContext.getUserId();
        userSubscriptionService.unfollowUser(userId, followeeId);
    }

    public CountResponse getFollowersCount(@NotNull long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    public CountResponse getFolloweesCount(@NotNull long followeeId) {
        return userSubscriptionService.getFolloweesCount(followeeId);
    }

    public List<UserDto> getFollowers(@NotNull long followeeId) {
        return userSubscriptionService.getFollowers(followeeId);
    }

    public List<UserDto> getFollowees(@NotNull long followerId) {
        return userSubscriptionService.getFollowees(followerId);
    }

}
