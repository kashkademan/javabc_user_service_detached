package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.subscription.SubscriptionDto;
import school.faang.user_service.dto.subscription.SubscriptionFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.subscription.SubscriptionMapper;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{userId}/subscriptions/follow")
    public ResponseEntity<Long> followUser(@PathVariable("userId") long followerId, @RequestParam long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return new ResponseEntity<>(
                followeeId,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{userId}/subscriptions/unfollow")
    public ResponseEntity<Long> unfollowUser(@PathVariable("userId") long followerId, @RequestParam long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return new ResponseEntity<>(
                followeeId,
                HttpStatus.OK
        );
    }

    @PostMapping("/{userId}/subscriptions/followers")
    public ResponseEntity<List<SubscriptionDto>> getFollowers(@PathVariable("userId") long followeeId,
                                                              @RequestBody SubscriptionFilterDto filterDto) {
        List<User> followers = subscriptionService.getFollowers(followeeId, filterDto);
        List<SubscriptionDto> dtoList = followers.stream()
                .map(SubscriptionMapper::userToSubscriptionDto)
                .toList();

        return new ResponseEntity<>(
                dtoList,
                HttpStatus.OK
        );
    }

    @GetMapping("{userId}/subscriptions/followersCount")
    public ResponseEntity<Long> getFollowersCount(@PathVariable("userId") long followeeId) {
        return new ResponseEntity<>(
                subscriptionService.getFollowersCount(followeeId),
                HttpStatus.OK
        );
    }

    @PostMapping("{userId}/subscriptions/following")
    public ResponseEntity<List<SubscriptionDto>> getFollowing(@PathVariable("userId") long followeeId,
                                                              @RequestBody SubscriptionFilterDto filterDto) {
        List<User> followees = subscriptionService.getFollowing(followeeId, filterDto);
        List<SubscriptionDto> dtoList = followees.stream()
                .map(SubscriptionMapper::userToSubscriptionDto)
                .toList();

        return new ResponseEntity<>(
                dtoList,
                HttpStatus.OK
        );
    }

    @GetMapping("{userId}/subscriptions/followingCount")
    public ResponseEntity<Integer> getFollowingCount(@PathVariable("userId") long followerId) {
        return new ResponseEntity<>(
                subscriptionService.getFollowingCount(followerId),
                HttpStatus.OK
        );
    }
}
