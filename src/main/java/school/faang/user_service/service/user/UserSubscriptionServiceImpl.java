package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void followUser(long followerId, long followeeId) {
        processSubscription(followerId, followeeId, SubscriptionType.FOLLOW, () ->
                subscriptionRepository.followUser(followerId, followeeId)
        );
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        processSubscription(followerId, followeeId, SubscriptionType.UNFOLLOW, () ->
                subscriptionRepository.unfollowUser(followerId, followeeId)
        );
    }

    @Override
    public CountResponse getFollowersCount(long followeeId) {
        log.info("Получение количества подписчиков пользователя: followeeId={}", followeeId);
        return new CountResponse(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId));
    }

    @Override
    public CountResponse getFolloweesCount(long followerId) {
        log.info("Получение количества подписок пользователя: followerId={}", followerId);
        return new CountResponse(subscriptionRepository.findFolloweesAmountByFollowerId(followerId));
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFiltersDto filters) {
        return getUserDto(() -> subscriptionRepository.findByFolloweeId(followeeId), filters);
    }

    @Override
    public List<UserDto> getFollowees(long followerId, UserFiltersDto filters) {
        return getUserDto(() -> subscriptionRepository.findByFollowerId(followerId), filters);
    }

    private void processSubscription(Long followerId,
                                     Long followeeId,
                                     SubscriptionType type,
                                     SubscriptionAction action) {
        String actionText = (type == SubscriptionType.FOLLOW) ? "подписки" : "отписки";
        log.info("Попытка {}: followerId={}, followeeId={}", actionText, followerId, followeeId);

        boolean follower = userRepository.existsById(followerId);
        boolean followee = userRepository.existsById(followeeId);

        if (follower && followee) {
            String selfActionText = (type == SubscriptionType.FOLLOW) ? "подписаться на" : "отписаться от";
            log.error("Пользователь попытался {} самого себя: id={}", selfActionText, followerId);
            throw new DataValidationException("Нельзя " + selfActionText + "на самого себя");
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

    private boolean applyFilters(User user, UserFiltersDto filters) {
        boolean matchesName = filters.getNamePattern() == null || user.getUsername().contains(filters.getNamePattern());

        boolean matchesPhone = filters.getPhonePattern() == null || user.getPhone().contains(filters.getPhonePattern());

        boolean matchesExperience = user.getExperience() >= filters.getExperienceMin()
                && user.getExperience() <= filters.getExperienceMax();
        return matchesName && matchesPhone && matchesExperience;
    }

    private List<UserDto> getUserDto(Supplier<Stream<User>> userStreamSupplier, UserFiltersDto filters) {
        try (Stream<User> stream = userStreamSupplier.get()) {
            return stream
                    .filter(user -> applyFilters(user, filters))
                    .map(userMapper::toUserDto)
                    .toList();
        }
    }
}
