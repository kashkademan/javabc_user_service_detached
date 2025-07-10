package school.faang.user_service.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RestController
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    @Autowired
    public UserSubscriptionController(UserSubscriptionService userSubscriptionService, UserContext userContext) {
        this.userSubscriptionService = userSubscriptionService;
        this.userContext = userContext;
    }

    @PostMapping("/follow")
    public void followUser(@RequestParam long followeeId) {
        long followerId = userContext.getUserId();
        userSubscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/unfollow")
    public void unfollowUser(@RequestParam long followeeId) {
        long followerId = userContext.getUserId();
        userSubscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers/count")
    public CountResponse getFollowersCount(@RequestParam long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/followees/count")
    public CountResponse getFolloweesCount() {
        long followerId = userContext.getUserId();
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/followers")
    public List<UserDto> getFollowers(@RequestParam long followeeId) {
        return getFollowers(followeeId);
    }

    @GetMapping("/followees")
    public List<UserDto> getFollowees(@RequestParam long followerId) {
        return getFollowees(followerId);
    }

}
