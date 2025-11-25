package school.faang.user_service.controller.subscription;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
@Validated
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;
    private final FollowerEventPublisher eventPublisher;

    @PostMapping("/following")
    public void followUser(@RequestParam @Positive long followeeId) {
        long followerId = userContext.getUserId();
        ensureFollowerAndFolloweeNotTheSameUser(followerId, followeeId);
        userSubscriptionService.followUser(followerId, followeeId);
        eventPublisher.publish(new FollowerEvent(followeeId, followerId, LocalDateTime.now()));
    }

    @PostMapping("/unfollowing")
    public void unfollowUser(@RequestParam @Positive long followeeId) {
        long followerId = userContext.getUserId();
        ensureFollowerAndFolloweeNotTheSameUser(followerId, followeeId);
        userSubscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/{followeeId}/followers/count")
    public CountResponse getFollowersCount(@PathVariable @Positive long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{followerId}/followees/count")
    public CountResponse getFolloweesCount(@PathVariable @Positive long followerId) {
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable @Positive long followeeId,
                                      @Valid @RequestBody UserFiltersDto userFiltersDto) {
        ensureUserFiltersDtoValid(userFiltersDto);
        return userSubscriptionService.getFollowers(followeeId, userFiltersDto);
    }

    @GetMapping("/{followerId}/followees")
    public List<UserDto> getFollowees(@PathVariable @Positive long followerId,
                                      @Valid @RequestBody UserFiltersDto userFiltersDto) {
        ensureUserFiltersDtoValid(userFiltersDto);
        return userSubscriptionService.getFollowees(followerId, userFiltersDto);
    }

    private void ensureFollowerAndFolloweeNotTheSameUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new ForbiddenException("user cannot follow or unfollow himself");
        }
    }

    private void ensureUserFiltersDtoValid(UserFiltersDto userFiltersDto) {
        Objects.requireNonNull(userFiltersDto, "user filters cannot be null");

        if (userFiltersDto.experienceMin() > userFiltersDto.experienceMax()) {
            throw new DataValidationException("experience min cannot be greater than experience max");
        }
    }
}
