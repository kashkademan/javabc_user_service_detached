package school.faang.user_service.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;

@Slf4j
@Service
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserSubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserMapper userMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.userMapper = userMapper;
    }

    @Override
    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("Пользователь попытался подписаться на самого себя.");
            return;
        }

        boolean alreadyFollower = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (alreadyFollower) {
            log.warn("Пользователь уже подписан на этого пользователя");
            return;
        }

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("Вы подписались на пользователя");
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("Пользователь не может отписаться от самого себя");
            return;
        }
        boolean signed = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (!signed) {
            log.info("Пользователь не был подписан, чтобы отписаться");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("Вы отписались от пользователя");
    }

    @Override
    public CountResponse getFollowersCount(long followeeId) {
        long count = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        return new CountResponse(count);
    }

    @Override
    public CountResponse getFolloweesCount(long followerId) {
        long count = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        log.info("Колучество подписчиков у пользователя {}", count);
        return new CountResponse(count);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId) {
        return subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getFollowees(long followerId) {
        return subscriptionRepository.findByFollowerId(followerId)
                .map(userMapper::toUserDto)
                .toList();
    }


}
