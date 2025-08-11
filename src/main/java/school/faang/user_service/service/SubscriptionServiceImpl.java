package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;

// import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.FolloweeSumProjection;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionEntityRepository;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionEntityRepository subscriptionEntityRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    @Override
    public void followUser(long followerId, long followeeId) {
        boolean isAlreadyFollowing = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isAlreadyFollowing) {
            throw new DataValidationException("You are already following this user");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(followers, userFilterDto)
                .map(userMapper::toUserDto)
                .toList();
    }

    private Stream<User> filterUsers(Stream<User> users, UserFilterDto userFilterDto) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(userFilterDto))
                .reduce(users,
                        (currentStream, filter) -> filter.apply(currentStream, userFilterDto),
                        Stream::concat);
    }

    @Override
    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Override
    public List<UserDto> getFollowing(long followerId, UserFilterDto userFilterDto) {
        Stream<User> followees = subscriptionRepository.findByFollowerId(followerId);
        return filterUsers(followees, userFilterDto)
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    @Override
    public int getAllSubscriptions() {
        return subscriptionRepository.getAllSubscriptions();
    }

    @Override
    public List<Integer> getAuthorsOrderdBySubscribers() {
        return subscriptionRepository.getAuthorsOrderdBySubscribers();
    }

    // @Override
    // public Page<FolloweeSumProjection> getFolloweesBySumOfFollowers(int page, int size) {
    //     return subscriptionEntityRepository.findFolloweesBySumOfFollowers(PageRequest.of(page, size));
    // }

    // @Override
    // public Page<Long> getDistinctFollowerIds(int page, int size) {
    //     return subscriptionEntityRepository.findDistinctFollowerIds(PageRequest.of(page, size));
    // }

    @Override
    public List<FolloweeSumProjection> getFolloweesBySumOfFollowers(int page, int size) {
        return subscriptionEntityRepository.findFolloweesBySumOfFollowers(PageRequest.of(page, size)).getContent();
    }

    @Override
    public List<Long> getDistinctFollowerIds(int page, int size) {
        return subscriptionEntityRepository.findDistinctFollowerIds(PageRequest.of(page, size)).getContent();
    }
}
