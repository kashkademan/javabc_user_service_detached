package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.dto.user.UserFiltersDto;

import java.util.List;

public interface UserSubscriptionService {
    void followUser(long followeeId);

    void unfollowUser(long followeeId);

    CountResponse getFollowersCount(long followeeId);

    CountResponse getFolloweesCount(long followerId);

    List<UserViewDto> getFollowers(long followeeId, UserFiltersDto filters);

    List<UserViewDto> getFollowees(long followerId, UserFiltersDto filters);

    List<Long> getFollowerIds(Long followee);
}
