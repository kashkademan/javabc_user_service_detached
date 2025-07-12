package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Override
    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new ForbiddenException("Пользователь попытался подписаться на самого себя.");
        }

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new ForbiddenException("Пользователь уже подписан на этого пользователя");
        }

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("Вы подписались на пользователя");
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new ForbiddenException("Пользователь не может отписаться от самого себя");
        }

        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new ForbiddenException("Пользователь не подписывался, чтобы отписаться.");
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
        log.info("Количество подписчиков у пользователя {}", count);
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
