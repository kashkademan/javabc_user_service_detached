package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.SubscriptionFilterDto;
import school.faang.user_service.entity.Subscription;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.subscription.SubscriptionFilter;
import school.faang.user_service.filter.subscription.SubscriptionUsernameFilters;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestServiceSubscription {

    @Mock
    private SubscriptionRepository repository;

    private SubscriptionFilterDto filterDto;
    private SubscriptionService service;

    @BeforeEach
    void setUp() {
        List<SubscriptionFilter> subscriptionFilters = new ArrayList<>();
        SubscriptionFilter mockFilter = new SubscriptionUsernameFilters();
        subscriptionFilters.add(mockFilter);
        filterDto = new SubscriptionFilterDto("name2", "000", 1, 10);
        service = new SubscriptionService(repository, subscriptionFilters);
    }

    @Test
    public void testFollowUserValidVariant() {
        long followerId = 1L;
        long followeeId = 6L;

        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        service.followUser(followerId, followeeId);

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(repository, times(1)).save(captor.capture());

        Subscription savedSub = captor.getValue();
        assertEquals(followerId, savedSub.getFollower_id());
        assertEquals(followeeId, savedSub.getFollowee_id());
    }

    @Test
    public void testFollowUserInvalidSameUserId() {
        long followerId = 1L;
        long followeeId = 1L;

        assertThrows(DataValidationException.class, () -> service.followUser(followerId, followeeId));
    }

    @Test
    public void testFollowUserInvalidSubAlreadyExists() {
        long followerId = 1L;
        long followeeId = 2L;

        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> service.followUser(followerId, followeeId));
    }

    @Test
    public void testUnfollowUserValidVariant() {
        long followerId = 1L;
        long followeeId = 6L;

        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        when(repository.getSubscription(followerId, followeeId)).thenReturn(Subscription.builder()
                .follower_id(followerId)
                .followee_id(followeeId)
                .build());

        service.unfollowUser(followerId, followeeId);

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(repository, times(1)).delete(captor.capture());

        Subscription savedSub = captor.getValue();
        assertEquals(followerId, savedSub.getFollower_id());
        assertEquals(followeeId, savedSub.getFollowee_id());
    }

    @Test
    public void testUnfollowUserInvalidSameUserId() {
        long followerId = 1L;
        long followeeId = 1L;

        assertThrows(DataValidationException.class, () -> service.unfollowUser(followerId, followeeId));
    }

    @Test
    public void testUnfollowUserInvalidSubAlreadyExists() {
        long followerId = 1L;
        long followeeId = 2L;

        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> service.unfollowUser(followerId, followeeId));
    }

    @Test
    public void testGetFollowersCount() {
        long followeeId = 1L;
        long expectedCount = 7L;

        when(repository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(expectedCount);
        Long actualCount = service.getFollowersCount(followeeId);

        assertEquals(expectedCount, actualCount);
        verify(repository, times(1)).findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    public void testGetFolloweesCount() {
        long followerId = 1L;
        long expectedCount = 7L;

        when(repository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expectedCount);
        Long actualCount = service.getFollowingCount(followerId);

        assertEquals(expectedCount, actualCount);
        verify(repository, times(1)).findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    void getFollowersShouldApplyFilters() {
        long followeeId = 1L;
        User user1 = User.builder().username("name1").phone("111").experience(10).build();
        User user2 = User.builder().username("name2").phone("222").experience(1).build();
        User user3 = User.builder().username("name3").phone("111").experience(100).build();
        List<User> allUsers = Arrays.asList(user1, user2, user3);
        List<User> expectedUsers = Collections.singletonList(user2);

        when(repository.findByFolloweeId(anyLong())).thenReturn(allUsers.stream());

        List<User> result = service.getFollowers(followeeId, filterDto);

        assertEquals(expectedUsers, result);
        verify(repository).findByFolloweeId(followeeId);
    }

}
