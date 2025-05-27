package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/{followerId}/follow/{followeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> followUser(@PathVariable("followerId") long followerId,
                                           @PathVariable("followeeId") long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> unfollowUser(@PathVariable("followerId") long followerId,
                                             @PathVariable("followeeId") long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{followeeId}/followers/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable("followeeId") long followeeId,
                                                      @ModelAttribute UserFilterDto filters) {
        List<UserDto> followers = subscriptionService.getFollowers(followeeId, filters);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("{followeeId}/followers/count")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> getFollowersCount(@PathVariable("followeeId") long followeeId) {
        int count = subscriptionService.getFollowersCount(followeeId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("{followerId}/following")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDto>> getFollowing(@PathVariable("followerId") long followerId,
                                                      @ModelAttribute UserFilterDto filter) {
        List<UserDto> following = subscriptionService.getFollowing(followerId, filter);
        return ResponseEntity.ok(following);
    }

    @GetMapping("{followerId}/following/count")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> getFollowingCount(@PathVariable("followerId") long followerId) {
        int count = subscriptionService.getFollowingCount(followerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{followeeId}/followers/ids")
    public ResponseEntity<List<Long>> getFollowerIds(@PathVariable long followeeId) {
        return ResponseEntity.ok(subscriptionService.getFollowerIds(followeeId));
    }
}