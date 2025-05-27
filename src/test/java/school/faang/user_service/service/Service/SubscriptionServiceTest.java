package school.faang.user_service.service.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.SubscriptionService;
import school.faang.user_service.service.subscription.filter.NameFilter;
import school.faang.user_service.service.subscription.filter.UserFilter;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private List<UserFilter> filters;

    @Mock
    private UserFilter nameFilter;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    public void testFollowUser_FollowsSelf() {
        long userId = 1;

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(userId, userId);
        });

        assertEquals("You can't subscribe or unsubscribe to yourself", exception.getMessage());
    }

    @Test
    void testFollowUser_FollowsUser() {
        long followerId = 1;
        long followeeId = 2;

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUser_success() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testUnfollowUser_unsuccess() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionService.unfollowUser(followerId, followeeId);
        });

        assertEquals("Subscription does not exist", exception.getMessage());
    }

    @Test
    public void testGetFollowers() {
        long followerId = 1;
        UserFilterDto filter = new UserFilterDto("Alex", null, null, null);
        List<UserDto> expected = List.of(new UserDto(followerId, null, null));

        User user = new User();
        user.setId(followerId);
        user.setUsername("Alex");
        List<User> users = List.of(user);

        when(filters.iterator()).thenReturn(List.<UserFilter>of().iterator());
        when(subscriptionRepository.findByFolloweeId(followerId)).thenReturn(users.stream());
        when(userMapper.toDto(user)).thenReturn(expected.get(0));

        List<UserDto> result = subscriptionService.getFollowers(followerId, filter);

        assertEquals(expected, result);
    }

    @Test
    public void testGetFollowing() {
        long followerId = 1;
        UserFilterDto filter = new UserFilterDto("Alex", null, null, null);
        List<UserDto> expected = List.of(new UserDto(followerId, null, null));

        User user = new User();
        user.setId(followerId);
        user.setUsername("Alex");
        List<User> users = List.of(user);

        when(filters.iterator()).thenReturn(List.<UserFilter>of().iterator());
        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(users.stream());
        when(userMapper.toDto(user)).thenReturn(expected.get(0));

        List<UserDto> result = subscriptionService.getFollowing(followerId, filter);

        assertEquals(expected, result);
    }

    @Test
    public void testGetFollowersCount() {
        long followerId = 1;
        int expected = 5;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followerId)).thenReturn(expected);

        int result = subscriptionService.getFollowersCount(followerId);

        assertEquals(expected, result);
    }

    @Test
    public void testGetFollowingCount() {
        long followerId = 1;
        int expected = 5;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expected);

        int result = subscriptionService.getFollowingCount(followerId);

        assertEquals(expected, result);
    }

    @Test
    public void testFilterUserWhenOneFilter() {
        List<User> users = List.of(new User(), new User());
        users.get(0).setUsername("Alex");
        users.get(1).setUsername("John");
        UserFilterDto filter = new UserFilterDto("Alex", null, null, null);

        when(filters.iterator()).thenReturn(List.of(nameFilter).iterator());
        when(nameFilter.isApplicable(filter)).thenReturn(true);
        when(nameFilter.apply(any(), eq(filter))).thenAnswer(
                inv -> ((Stream<User>) inv.getArgument(0))
                        .filter(u -> u.getUsername().equals("Alex")));

        List<User> result = subscriptionService.filterUser(users, filter);

        assertEquals(1, result.size());
        assertEquals("Alex", result.get(0).getUsername());
    }

    @Test
    public void testFilterUser_whenNoFilterApplicable_returnsOriginalList() {
        List<User> users = List.of(new User(), new User());
        UserFilterDto filterDto = new UserFilterDto(null, null,
                null, null);

        when(filters.iterator()).thenReturn(List.of(nameFilter).iterator());
        when(nameFilter.isApplicable(filterDto)).thenReturn(false);

        List<User> result = subscriptionService.filterUser(users, filterDto);

        assertEquals(users, result);
    }
}
