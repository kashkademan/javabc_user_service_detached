package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.FolloweeSumProjection;

import java.util.List;

// import org.springframework.data.domain.Page;

public interface SubscriptionService {

    void followUser(long followerId, long followeeId);

    void unfollowUser(long followerId, long followeeId);

    List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto);

    int getFollowersCount(long followeeId);

    List<UserDto> getFollowing(long followerId, UserFilterDto userFilterDto);

    int getFollowingCount(long followerId);
    
    int getAllSubscriptions(); 

    List<Integer> getAuthorsOrderdBySubscribers();

    // Page<FolloweeSumProjection> getFolloweesBySumOfFollowers(int page, int size);

    // Page<Long> getDistinctFollowerIds(int page, int size);

    List<FolloweeSumProjection> getFolloweesBySumOfFollowers(int page, int size);

    List<Long> getDistinctFollowerIds(int page, int size);
}
