package school.faang.user_service.service.user;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

import java.util.List;

public interface SubscriptionService {

    List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto);

    int getFollowersCount(long followeeId);

    List<Long> getFollowersIds(long followeeId);
}
