package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

/**
 * Interface representing user subscription operations such as following and unfollowing users,
 * as well as retrieving follower and followee information.
 */
public interface UserSubscriptionService {

    /**
     * Makes the user with {@code followerId} follow the user with {@code followeeId}.
     *
     * @param followerId the ID of the user who wants to follow
     * @param followeeId the ID of the user to be followed
     */
    void followUser(long followerId, long followeeId);

    /**
     * Makes the user with {@code followerId} unfollow the user with {@code followeeId}.
     *
     * @param followerId the ID of the user who wants to unfollow
     * @param followeeId the ID of the user to be unfollowed
     */
    void unfollowUser(long followerId, long followeeId);

    /**
     * Retrieves the number of followers for the user with {@code followeeId}.
     *
     * @param followeeId the ID of the user whose followers count is requested
     * @return a {@link CountResponse} containing the number of followers
     */
    CountResponse getFollowersCount(long followeeId);

    /**
     * Retrieves the number of users that the user with {@code followerId} is following.
     *
     * @param followerId the ID of the user whose followees count is requested
     * @return a {@link CountResponse} containing the number of followees
     */
    CountResponse getFolloweesCount(long followerId);

    /**
     * Retrieves a list of users who are followers of the user with {@code followeeId}.
     *
     * @param followeeId the ID of the user whose followers are to be retrieved
     * @return a list of {@link UserDto} representing the followers
     */
    List<UserDto> getFollowers(long followeeId);

    /**
     * Retrieves a list of users that the user with {@code followerId} is following.
     *
     * @param followerId the ID of the user whose followees are to be retrieved
     * @return a list of {@link UserDto} representing the followees
     */
    List<UserDto> getFollowees(long followerId);
}
