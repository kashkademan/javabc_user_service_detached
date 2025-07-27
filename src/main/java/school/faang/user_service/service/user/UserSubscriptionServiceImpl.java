package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final UserContext userContext;
    private final List<UserFilter> filters;

    @Override
    @Transactional
    public void followUser(long followeeId) {
        long followerId = userContext.getUserId();
        processSubscription(
                followerId,
                followeeId,
                SubscriptionType.FOLLOW,
                () -> subscriptionRepository.followUser(followerId, followeeId)
        );
    }

    @Override
    public void unfollowUser(long followeeId) {
        long followerId = userContext.getUserId();
        processSubscription(
                followerId,
                followeeId,
                SubscriptionType.UNFOLLOW,
                () -> subscriptionRepository.unfollowUser(followerId, followeeId)
        );
    }

    @Override
    public CountResponse getFollowersCount(long followeeId) {
        log.info("Получение количества подписчиков пользователя: followeeId={}", followeeId);
        long count = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        return new CountResponse(count);
    }

    @Override
    public CountResponse getFolloweesCount(long followerId) {
        log.info("Получение количества подписок пользователя: followerId={}", followerId);
        long count = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        return new CountResponse(count);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFiltersDto userFiltersDto) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();
        return getFilterUserDto(followers, userFiltersDto);
    }

    @Override
    public List<UserDto> getFollowees(long followerId, UserFiltersDto userFiltersDto) {
        List<User> followees = subscriptionRepository.findByFollowerId(followerId).toList();
        return getFilterUserDto(followees, userFiltersDto);
    }

    private void processSubscription(Long followerId,
                                     Long followeeId,
                                     SubscriptionType type,
                                     SubscriptionAction action) {
        String actionText = (type == SubscriptionType.FOLLOW) ? "подписки" : "отписки";
        log.info("Попытка {}: followerId={}, followeeId={}", actionText, followerId, followeeId);

        if (followerId.equals(followeeId)) {
            String selfActionText = (type == SubscriptionType.FOLLOW) ? "подписаться на" : "отписаться от";
            log.error("Пользователь попытался {} самого себя: id={}", selfActionText, followerId);
            throw new DataValidationException("Нельзя " + selfActionText + " самого себя");
        }

        boolean alreadyFollowed = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (type == SubscriptionType.FOLLOW && alreadyFollowed) {
            log.error("Уже подписан: followerId={}, followeeId={}", followerId, followeeId);
            throw new DataValidationException("Вы уже подписаны на этого пользователя");
        }

        if (type == SubscriptionType.UNFOLLOW && !alreadyFollowed) {
            log.error("Не подписан: followerId={}, followeeId={}", followerId, followeeId);
            throw new DataValidationException("Вы не подписаны на этого пользователя");
        }

        action.execute();

        log.info("Попытка {} - успех: followerId={}, followeeId={}", actionText, followerId, followeeId);
    }

    private List<UserDto> getFilterUserDto(List<User> users, UserFiltersDto userFiltersDto) {
        Stream<User> filteredUsers = users.stream();

        for (UserFilter userFilter : this.filters) {
            if (userFilter.isApplicable(userFiltersDto)) {
                filteredUsers = userFilter.apply(filteredUsers, userFiltersDto);
            }
        }

        return filteredUsers
                .map(mapper::toUserDto)
                .toList();
    }
}
