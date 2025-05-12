package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.subscription.SubscriptionFilterDto;
import school.faang.user_service.entity.Subscription;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.subscription.SubscriptionFilter;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.subscription.SubscriptionValidation;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final List<SubscriptionFilter> subscriptionFilters;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        boolean existSub = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        SubscriptionValidation.validateFollowAction(followerId, followeeId);
        SubscriptionValidation.validateSubscribeAction(existSub);

        subscriptionRepository.save(Subscription.builder()
                .follower_id(followerId)
                .followee_id(followeeId)
                .build()
        );
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        boolean existSub = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        SubscriptionValidation.validateUnfollowAction(followerId, followeeId);
        SubscriptionValidation.validateUnsubscribeAction(existSub);

        subscriptionRepository.delete(Subscription.builder()
                .follower_id(followerId)
                .followee_id(followeeId)
                .build()
        );
    }

    @Transactional(readOnly = true)
    public List<User> getFollowers(long followeeId, SubscriptionFilterDto filterDto) {
        Stream<User> userStream = subscriptionRepository.findByFolloweeId(followeeId);

        return filterUsers(userStream, filterDto);
    }

    @Transactional(readOnly = true)
    public Long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public List<User> getFollowing(long followeeId, SubscriptionFilterDto filterDto) {
        Stream<User> userStream = subscriptionRepository.findByFollowerId(followeeId);

        return filterUsers(userStream, filterDto);
    }

    @Transactional(readOnly = true)
    public Integer getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<SubscriptionFilter> getApplicableFilters(SubscriptionFilterDto filterDto) {
        return subscriptionFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .toList();
    }

    private boolean applyFilters(User user, SubscriptionFilterDto filterDto) {
        List<SubscriptionFilter> filters = getApplicableFilters(filterDto);

        return filters.stream().allMatch(filter -> filter.apply(user, filterDto));
    }

    private List<User> filterUsers(Stream<User> users, SubscriptionFilterDto filterDto) {
        return users
                .filter((user) -> applyFilters(user, filterDto))
                .toList();
    }
}
