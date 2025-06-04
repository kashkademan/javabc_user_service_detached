package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.FollowRequest;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@Controller
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
@Validated
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public void followUser(@RequestBody FollowRequest followRequest) {
        subscriptionService.followUser(followRequest.followerId(), followRequest.followeeId());
    }

    @PostMapping("/unfollow")
    public void unfollowUser(@RequestBody FollowRequest followRequest) {
        subscriptionService.unfollowUser(followRequest.followerId(), followRequest.followeeId());
    }

    @GetMapping("/followers/{followeeId}")
    public List<UserDto> getFollowers(
            @PathVariable @Min(value = 1, message = "id must be a positive number") long followeeId,
            @Valid @ModelAttribute UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/followers/count/{followeeId}")
    public int getFollowersCount(@PathVariable @Min(value = 1, message = "id must be a positive number") long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/following/{followeeId}")
    public List<UserDto> getFollowing(
            @PathVariable @Min(value = 1, message = "id must be a positive number") long followeeId,
            @Valid @ModelAttribute UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping("/following/{followeeId}/count")
    public int getFollowingCount(@PathVariable @Min(value = 1, message = "id must be a positive number") long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }
}
