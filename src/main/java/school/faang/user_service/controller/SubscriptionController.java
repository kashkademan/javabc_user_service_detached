package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/follow")
    public void followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot follow yourself");
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/unfollow")
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot unfollow yourself");
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers")
    public List<UserDto> getFollowers(@RequestParam long followeeId, @ModelAttribute UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/followers/count")
    public int getFollowersCount(@RequestParam long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/following")
    public List<UserDto> getFollowing(@RequestParam long followerId, @ModelAttribute UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @GetMapping("/following/count")
    public int getFollowingCount(@RequestParam long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
