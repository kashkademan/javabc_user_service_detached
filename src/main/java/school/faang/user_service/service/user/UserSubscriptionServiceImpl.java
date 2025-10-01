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

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Override
    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("Пользователь {} попытался подписаться на самого себя", followerId);
            throw new DataValidationException("Пользователь не может подписаться на самого себя");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("Пользователь {} уже подписан на пользователя {}", followerId, followeeId);
            throw new DataValidationException("Вы уже подписаны на данного пользователя");
        }

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("Пользователь {} успешно подписался на пользователя {}",
                followerId, followeeId);
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("Пользователь {} попытался отписаться от самого себя", followerId);
            throw new DataValidationException("Пользователь не может отписаться от самого себя");
        }
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("Пользователь {} не подписан на пользователя {}", followerId, followeeId);
            throw new DataValidationException("Пользователь не подписан на данного пользователя");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("Пользователь {} успешно отписался от пользователя {}", followerId, followeeId);
    }

    @Override
    public CountResponse getFollowersCount(long followeeId) {
        long count = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        log.debug("Количество подписчиков пользователя {}: {}", followeeId, count);
        return new CountResponse(count);
    }

    @Override
    public CountResponse getFolloweesCount(long followerId) {
        long count = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        log.debug("Количество подписок пользователя {}: {}", followerId, count);
        return new CountResponse(count);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFiltersDto filters) {
        Stream<User> followersStream = subscriptionRepository.findByFolloweeId(followeeId);

        return applyFilters(followersStream, filters)
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getFollowees(long followerId, UserFiltersDto filters) {
        Stream<User> followeesStream = subscriptionRepository.findByFollowerId(followerId);

        return applyFilters(followeesStream, filters)
                .map(userMapper::toUserDto)
                .toList();
    }

    private Stream<User> applyFilters(Stream<User> usersStream, UserFiltersDto filters) {
        Stream<User> filteredStream = usersStream;

        if (filters.namePattern() != null && !filters.namePattern().isBlank()) {
            filteredStream = filteredStream.filter(user ->
                    user.getUsername().toLowerCase().contains(filters.namePattern().toLowerCase()));
        }

        if (filters.phonePattern() != null && !filters.phonePattern().isBlank()) {
            filteredStream = filteredStream.filter(user ->
                    user.getPhone() != null && user.getPhone().contains(filters.phonePattern()));
        }

        filteredStream = filteredStream.filter(user ->
                user.getExperience() >= filters.experienceMin()
                        && user.getExperience() <= filters.experienceMax());

        return filteredStream;
    }
}