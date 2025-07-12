package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    @PostMapping("/Subscription")
    public void followUser(@RequestParam long followeeId) {
        long followerId = userContext.getUserId();
        userSubscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/disclaimer")
    public void unfollowUser(@Valid @RequestParam long followeeId) {
        long followerId = userContext.getUserId();
        userSubscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers/count")
    public CountResponse getFollowersCount(@Valid @RequestParam long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/followees/count")
    public CountResponse getFolloweesCount() {
        long followerId = userContext.getUserId();
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/followers")
    public List<UserDto> getFollowers(@Valid @RequestParam long followeeId) { return getFollowers(followeeId);
    }

    @GetMapping("/followees")
    public List<UserDto> getFollowees(@Valid @RequestParam long followerId) {
        return getFollowees(followerId);
    }

}
