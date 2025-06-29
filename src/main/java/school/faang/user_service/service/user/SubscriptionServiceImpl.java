package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(followers, userFilterDto)
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getFollowersIds(long followeeId) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return followers.map(User::getId).collect(Collectors.toList());
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
}
