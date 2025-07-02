package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Override
    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.error("user {} is trying to re-subscribe to user {}", followerId, followeeId);
            throw new DataValidationException("the subscription already exists");
        }

        if (followerId == followeeId) {
            log.error("user {} is trying to subscribe to himself", followerId);
            throw new DataValidationException("subscribe to yourself");
        }

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("user {} subscribed to user {}", followerId, followeeId);
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.error("the user {} is trying to unsubscribe from the user {}", followerId, followeeId);
            throw new DataValidationException("the subscription does not exists");
        }

        if (followerId == followeeId) {
            log.error("user {} is trying to unsubscribe to himself", followerId);
            throw new DataValidationException("unsubscribe to yourself");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("user {} unsubscribed from user {}", followerId, followeeId);
    }

    @Override
    public CountResponse getFollowersCount(long followeeId) {
        CountResponse countResponse = new CountResponse();
        countResponse.setCount(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId));
        log.info("Get followers count");
        return countResponse;
    }

    @Override
    public CountResponse getFolloweesCount(long followerId) {
        CountResponse countResponse = new CountResponse();
        countResponse.setCount(subscriptionRepository.findFolloweesAmountByFollowerId(followerId));
        log.info("Get followees count");
        return countResponse;
    }

    @Override
    public List<UserDto> getFollowers(long followeeId) {
        List<User> followersList = subscriptionRepository.findByFolloweeId(followeeId).toList();
        log.info("Get followers");
        return followersList.stream()
                .map(user -> userMapper.toUserDto(user))
                .toList();
    }

    @Override
    public List<UserDto> getFollowees(long followerId) {
        List<User> followeesList = subscriptionRepository.findByFollowerId(followerId).toList();
        log.info("Get followees count");
        return followeesList.stream()
                .map(user -> userMapper.toUserDto(user))
                .toList();
    }
}
