package school.faang.user_service.service.subscription.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.subscription.SubscriptionController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;

    private UserDtoFilter userDtoFilter;


    @Test
    void testFollowUser_Success() {
        subscriptionController.followUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionService, times(1)).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void testUnfollowUser_Success() {
        subscriptionController.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionService, times(1)).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    void testGetFollowers_Success() {
        userDtoFilter = new UserDtoFilter("NN","947",1,10);
        List<UserDto> expected = List.of(
                new UserDto(),
                new UserDto()
        );
        when(subscriptionService.getFollowers(FOLLOWEE_ID,userDtoFilter)).thenReturn(expected);
        List<UserDto> result = subscriptionController.getFollowers(FOLLOWEE_ID,userDtoFilter);
        assertEquals(expected, result);
        verify(subscriptionService, times(1)).getFollowers(FOLLOWEE_ID,userDtoFilter);
    }

    @Test
    void testGetFollowerCount_Success() {
        subscriptionController.getFollowerCount(FOLLOWER_ID);
        verify(subscriptionService, times(1)).getFollowerCount(FOLLOWER_ID);
    }

    @Test
    void getFollowerCountTest(){
        int expected = 1;
        when(subscriptionService.getFollowerCount(FOLLOWER_ID)).thenReturn(expected);
        int result = subscriptionController.getFollowerCount(FOLLOWER_ID);
        assertEquals(expected, result);
    }

    @Test
    void getFollowingTest(){
        userDtoFilter = new UserDtoFilter("NN","947",1,10);
        List<UserDto> expected = List.of(
                new UserDto(),
                new UserDto()
        );
        when(subscriptionService.getFollowing(FOLLOWER_ID,userDtoFilter)).thenReturn(expected);
        List<UserDto> result = subscriptionController.getFollowing(FOLLOWER_ID,userDtoFilter);
        assertEquals(expected, result);
        verify(subscriptionService, times(1)).getFollowing(FOLLOWER_ID,userDtoFilter);
    }

    @Test
    void getFollowingCountTest(){
        int expected = 1;
        when(subscriptionService.getFollowingCount(FOLLOWER_ID)).thenReturn(expected);
        int result = subscriptionController.getFollowingCount(FOLLOWER_ID);
        assertEquals(expected, result);
    }
}
