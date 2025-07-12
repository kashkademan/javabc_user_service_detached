package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserSubscriptionService;
import school.faang.user_service.service.user.UserSubscriptionServiceImpl;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("followers")
public class UserSubscriptionController {
    private final UserSubscriptionServiceImpl subscriptionService;
    private final UserContext userContext;

    @PostMapping("/{followeeId}")
    public ResponseEntity<Void> followUser(@PathVariable Long followeeId) {
            Long followerId = userContext.getUserId();
            subscriptionService.followUser(followerId, followeeId);
            return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{followeeId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long followeeId) {
        Long followerId = userContext.getUserId();
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{followeeId}/followers-count")
    public ResponseEntity<CountResponse> getFollowersCount(@PathVariable Long followeeId) {
        return ResponseEntity.ok(subscriptionService.getFollowersCount(followeeId));
    }

    @GetMapping("{followerId}/followees-count")
    public ResponseEntity<CountResponse> getFolloweesCount(@PathVariable Long followerId) {
        return ResponseEntity.ok(subscriptionService.getFolloweesCount(followerId));
    }

    @GetMapping("/{followeeId}/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable Long followeeId) {
        return ResponseEntity.ok(subscriptionService.getFollowers(followeeId));
    }

    @GetMapping("/{followerId}/followees")
    public ResponseEntity<List<UserDto>> getFollowees(@PathVariable Long followerId) {
        return ResponseEntity.ok(subscriptionService.getFollowees(followerId));
    }
}
