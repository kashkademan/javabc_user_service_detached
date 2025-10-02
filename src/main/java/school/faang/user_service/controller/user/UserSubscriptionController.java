package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserSubscriptionService;
import school.faang.user_service.config.context.UserContext;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    @PostMapping("/follow/{followeeId}")
    public ResponseEntity<String> followUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        try {
            userSubscriptionService.followUser(followerId, followeeId);
            return ResponseEntity.ok("User with ID " + followerId + " now follows user with ID " + followeeId);
        } catch (DataValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/unfollow/{followeeId}")
    public ResponseEntity<String> unfollowUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        try {
            userSubscriptionService.unfollowUser(followerId, followeeId);
            return ResponseEntity.ok("User with ID " + followerId + " no longer follows user with ID " + followeeId);
        } catch (DataValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/followers/count/{followeeId}")
    public CountResponse getFollowersCount(@PathVariable long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/followees/count/{followerId}")
    public CountResponse getFolloweesCount(@PathVariable long followerId) {
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/followers/{followeeId}")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFiltersDto userFiltersDto) {
        return userSubscriptionService.getFollowers(followeeId, userFiltersDto);
    }

    @GetMapping("/followees/{followerId}")
    public List<UserDto> getFollowees(@PathVariable long followerId, @RequestBody UserFiltersDto userFiltersDto) {
        return userSubscriptionService.getFollowees(followerId, userFiltersDto);
    }
}
