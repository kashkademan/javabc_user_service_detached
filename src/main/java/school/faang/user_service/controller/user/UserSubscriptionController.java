package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subscriptions")
public class UserSubscriptionController {
    private final UserSubscriptionService service;
    private final UserContext userContext;

    @PostMapping("/follow/{followeeId}")
    public ResponseEntity<Void> followUser(@PathVariable Long followeeId) {
        service.followUser(followeeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/unfollow/{followeeId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long followeeId) {
        service.unfollowUser(followeeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/followers/count")
    public ResponseEntity<CountResponse> getFollowersCount(@RequestParam Long followeeId) {
        return ResponseEntity.ok(service.getFollowersCount(followeeId));
    }

    @GetMapping("/followees/count")
    public ResponseEntity<CountResponse> getFolloweesCount(@RequestParam Long followerId) {
        return ResponseEntity.ok(service.getFolloweesCount(followerId));
    }

    @GetMapping("/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@RequestParam Long followeeId,
                                                      @ModelAttribute UserFiltersDto filters) {
        return ResponseEntity.ok(service.getFollowers(followeeId, filters));
    }

    @GetMapping("/followees")
    public ResponseEntity<List<UserDto>> getFollowees(@RequestParam Long followerId,
                                                      @ModelAttribute UserFiltersDto filters) {
        return ResponseEntity.ok(service.getFollowees(followerId, filters));
    }
}
