package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.event.FollowEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserFilterMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.messaging.EventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserFilterMapper userFilterMapper;
    private final UserMapper userMapper;
    private final EventPublisher<FollowEventDto> eventPublisher;

    @Override
    public void followUser(long followerId, long followeeId) {
        ensureSubscriptionStateValidation(followerId, followeeId, false);
        subscriptionRepository.followUser(followerId, followeeId);
        User follower = userRepository.findById(followerId).orElseThrow(() ->
                new UserNotFoundException("User with id %d not found".formatted(followerId)));
        User followee = userRepository.findById(followeeId).orElseThrow(() ->
                new UserNotFoundException("User with id %d not found".formatted(followeeId)));
        follower.getFollowees().add(followee);
        followee.getFollowers().add(follower);

        FollowEventDto followEvent = new FollowEventDto(followeeId, followerId);
        eventPublisher.publish(followEvent);
    }

    @Override
    public void unfollowUser(long followerId, long targetId) {
        ensureSubscriptionStateValidation(followerId, targetId, true);
        subscriptionRepository.unfollowUser(followerId, targetId);
    }

    @Override
    public List<UserDto> getFollowers(long id, UserFilterDto filterDto) {
        return subscriptionRepository.findByFolloweeId(id)
                .filter(userFilterMapper.toEntity(filterDto))
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getFollowing(long id, UserFilterDto filterDto) {
        return subscriptionRepository.findByFollowerId(id)
                .filter(userFilterMapper.toEntity(filterDto))
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public long getFollowersCount(long id) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(id);
    }

    @Override
    public long getFollowingCount(long id) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(id);
    }

    private void ensureSubscriptionStateValidation(long followerId, long followeeId, boolean shouldExist) {
        if (followerId == followeeId)
            throw new DataValidationException("A user cannot follow themselves. UserId: " + followeeId);
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId) != shouldExist)
            throw new DataValidationException("The subscription has already been issued");
    }
}