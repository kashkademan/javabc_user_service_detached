package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.subscription.SubscriptionService;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionValidator subscriptionValidator;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateFollowUserIds(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateUnfollowUserIds(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        List<User> followers = subscriptionService.getFollowers(followeeId, filter);
        return followers.stream().map(userMapper::toDto).toList();
    }

    @GetMapping("/followers/{followeeId}")
    public List<Long> getFollowersIds(@PathVariable long followeeId) {
        return subscriptionService.getFollowersIds(followeeId);
    }

    @GetMapping("/followees/{followerId}")
//    @Transactional(readOnly = true)
    public List<UserDto> getFollowees(@PathVariable long followerId) {
        List<User> followees = subscriptionService.getFollowees(followerId);
        return followees.stream().map(userMapper::toDto).toList();
    }

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }
}
