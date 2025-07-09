package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
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
    @Transactional
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
    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
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
    @Transactional
    public CountResponse getFollowersCount(long followeeId) {
        long count = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        return new CountResponse(count);
    }

    @Override
    @Transactional
    public CountResponse getFolloweesCount(long followerId) {
        CountResponse countResponse = new CountResponse(
                subscriptionRepository.findFolloweesAmountByFollowerId(followerId));
        return countResponse;
    }

    @Override
    @Transactional
    public List<UserDto> getFollowers(long followeeId) {
        log.info("Get followers");
        return subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public List<UserDto> getFollowees(long followerId) {
        log.info("Get followees");
        return subscriptionRepository.findByFollowerId(followerId)
                .map(userMapper::toUserDto)
                .toList();
    }
}
