package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new IllegalArgumentException("This subscriber already exists");
        }

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new IllegalArgumentException("You are not subscribed to this user");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<User> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);

        return filterUsers(followers, filters);
    }

    public List<User> getFollowees(long followerId) {
        Stream<User> followees = subscriptionRepository.findFollowees(followerId);
        return followees.toList();
    }

    public List<Long> getFollowersIds(long followeeId) {
        return subscriptionRepository.findFollowersIds(followeeId);
    }

    public List<User> filterUsers(Stream<User> users, UserFilterDto filters) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(users, filters).stream())
                .skip((long) filters.getPage() * filters.getPageSize())
                .limit(filters.getPageSize())
                .toList();
    }

    public int getFollowersCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
