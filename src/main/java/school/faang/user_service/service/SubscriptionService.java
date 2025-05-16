package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoFilter;

import java.util.List;

public interface SubscriptionService {
    void followUser(long followerId, long followeeId);
    void unfollowUser(long followerId, long followeeId);
    List<UserDto> getFollowers(long followeeId, UserDtoFilter filter);
    int getFollowerCount(long followerId);
    List<UserDto> getFollowing(long followerId, UserDtoFilter filter);
    int getFollowingCount(long followerId);
}
