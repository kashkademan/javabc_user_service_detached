package school.faang.user_service.service.Controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.dto.FollowRequest;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    public void testfollowUser() {
        FollowRequest request = new FollowRequest(1, 2);

        subscriptionController.followUser(request);

        verify(subscriptionService).followUser(1, 2);
    }

    @Test
    public void testunfollowUser() {
        FollowRequest request = new FollowRequest(1, 2);

        subscriptionController.unfollowUser(request);

        verify(subscriptionService).unfollowUser(1, 2);
    }

    @Test
    public void testGetFollowers() {
        long followerId = 1;
        UserFilterDto filter = new UserFilterDto("John", null,
                null, null);
        List<UserDto> expected = List.of(new UserDto(followerId, "John", null));

        when(subscriptionService.getFollowers(followerId, filter)).thenReturn(expected);

        List<UserDto> result = subscriptionController.getFollowers(followerId, filter);

        assertEquals(expected, result);

        verify(subscriptionService).getFollowing(followerId, filter);
    }

    @Test
    public void testGetFollowersCount() {
        long followeeId = 1;
        int expected = 5;

        when(subscriptionService.getFollowersCount(followeeId)).thenReturn(5);

        int result = subscriptionController.getFollowersCount(followeeId);

        assertEquals(expected, result);

        verify(subscriptionService).getFollowingCount(followeeId);
    }

    @Test
    public void testGetFollowing() {
        long followeeId = 1;
        UserFilterDto filter = new UserFilterDto("Alice", null,
                null, null);
        List<UserDto> expected = List.of(new UserDto(followeeId, "Alice", null));

        when(subscriptionService.getFollowing(followeeId, filter)).thenReturn(expected);

        List<UserDto> result = subscriptionController.getFollowing(followeeId, filter);

        assertEquals(expected, result);

        verify(subscriptionService).getFollowing(followeeId, filter);
    }

    @Test
    public void testGetFollowingCount() {
        long followeeId = 1;
        int expected = 10;

        when(subscriptionService.getFollowingCount(followeeId)).thenReturn(expected);

        int result = subscriptionController.getFollowingCount(followeeId);

        assertEquals(expected, result);

        verify(subscriptionService).getFollowingCount(followeeId);
    }
}
