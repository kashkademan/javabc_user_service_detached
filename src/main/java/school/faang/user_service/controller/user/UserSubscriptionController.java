package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserSubscriptionController {
    private final UserSubscriptionService subscriptionService;
    private final UserContext userContext;

    @PostMapping("/{followeeId}")
    public void followUser(@PathVariable Long followeeId) {
        Long followerId = userContext.getUserId();
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followeeId}")
    public void unfollowUser(@PathVariable Long followeeId) {
        Long followerId = userContext.getUserId();
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/{followeeId}/followers/count")
    public CountResponse getFollowersCount(@PathVariable Long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{followerId}/followees/count")
    public CountResponse getFolloweesCount(@PathVariable Long followerId) {
        return subscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable Long followeeId) {
        return subscriptionService.getFollowers(followeeId);
    }

    @GetMapping("/{followerId}/followees")
    public List<UserDto> getFollowees(@PathVariable Long followerId) {
        return subscriptionService.getFollowees(followerId);
    }
}
