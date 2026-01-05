package school.faang.user_service.controller.subscription;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.event.FollowerEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.service.subscription.UserSubscriptionService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;
    private final FollowerEventPublisher eventPublisher;

    @PostMapping("/{userId}/followers")
    public ResponseEntity<Void> followUser(@PathVariable("userId") @Positive long followeeId) {
        long followerId = userContext.getUserId();
        ensureFollowerAndFolloweeNotTheSameUser(followerId, followeeId);
        userSubscriptionService.followUser(followerId, followeeId);
        eventPublisher.publish(new FollowerEvent(followeeId, followerId, LocalDateTime.now()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/followers")
    public ResponseEntity<Void> unfollowUser(@PathVariable("userId") @Positive long followeeId) {
        long followerId = userContext.getUserId();
        ensureFollowerAndFolloweeNotTheSameUser(followerId, followeeId);
        userSubscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/followers/count")
    public CountResponse getFollowersCount(@PathVariable("userId") @Positive long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{userId}/followees/count")
    public CountResponse getFolloweesCount(@PathVariable("userId") @Positive long followerId) {
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/{userId}/followers")
    public List<UserDto> getFollowers(
            @PathVariable("userId") @Positive long followeeId,
            @ModelAttribute UserFiltersDto userFiltersDto
    ) {
        ensureUserExperienceFilterValid(userFiltersDto);
        return userSubscriptionService.getFollowers(followeeId, userFiltersDto);
    }

    @GetMapping("/{userId}/followees")
    public List<UserDto> getFollowees(
            @PathVariable("userId") @Positive long followerId,
            @ModelAttribute UserFiltersDto userFiltersDto
    ) {
        ensureUserExperienceFilterValid(userFiltersDto);
        return userSubscriptionService.getFollowees(followerId, userFiltersDto);
    }

    private void ensureFollowerAndFolloweeNotTheSameUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new ForbiddenException("user cannot follow or unfollow himself");
        }
    }

    private void ensureUserExperienceFilterValid(UserFiltersDto userFiltersDto) {
        if (userFiltersDto.experienceMin() > userFiltersDto.experienceMax()) {
            throw new DataValidationException("experience min cannot be greater than experience max");
        }
    }
}
