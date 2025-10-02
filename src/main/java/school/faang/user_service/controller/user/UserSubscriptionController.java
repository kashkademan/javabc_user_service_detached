package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Slf4j
@RequiredArgsConstructor
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    @PostMapping("/follow/{followeeId}")
    public void followUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();

        if (followerId == followeeId) {
            throw new DataValidationException("You can't subscribe to yourself");
        }

        log.info("The user {} подписывается на {}", followerId, followeeId);
        userSubscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/unfollow/{followeeId}")
    public void unfollowUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();

        if (followerId == followeeId) {
            throw new DataValidationException("You can't unsubscribe from yourself");
        }

        log.info("The user {} unsubscribes from {}", followerId, followeeId);
        userSubscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/{followeeId}/followers/count")
    public CountResponse getFollowersCount(@PathVariable long followeeId) {
        log.debug("Requesting the number of subscribers for a user: {}", followeeId);
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{followerId}/followees/count")
    public CountResponse getFolloweesCount(@PathVariable long followerId) {
        log.debug("Requesting the number of subscriptions for a user: {}", followerId);
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable long followeeId) {
        log.debug("Requesting a list of subscribers for a user: {}", followeeId);
        return userSubscriptionService.getFollowers(followeeId,
                new UserFiltersDto(null, null, 0, Integer.MAX_VALUE));
    }

    @GetMapping("/{followerId}/followees")
    public List<UserDto> getFollowees(@PathVariable long followerId) {
        log.debug("Requesting a list of subscriptions for a user: {}", followerId);
        return userSubscriptionService.getFollowees(followerId,
                new UserFiltersDto(null, null, 0, Integer.MAX_VALUE));
    }
}
