package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

    @PostMapping("/{followerId}/follow/{followeeId}")
    public void followUser(@PathVariable("followerId") long followerId,
                                           @PathVariable("followeeId") long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    public void unfollowUser(@PathVariable("followerId") long followerId,
                                             @PathVariable("followeeId") long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers/{followeeId}")
    public List<UserDto> getFollowers(@PathVariable("followeeId") long followeeId, UserDtoFilter userDtoFilter) {
        return subscriptionService.getFollowers(followeeId, userDtoFilter);
    }

    @GetMapping("/followers/{followerId}/count")
    public int getFollowerCount(@PathVariable("followerId") long followerId) {
        return subscriptionService.getFollowerCount(followerId);
    }

    @GetMapping("/{followerId}")
    public List<UserDto> getFollowing(@PathVariable("followerId") long followerId, UserDtoFilter userDtoFilter) {
        return subscriptionService.getFollowing(followerId, userDtoFilter);
    }

    @GetMapping("/{followerId}/count")
    public int getFollowingCount(@PathVariable("followerId") long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
