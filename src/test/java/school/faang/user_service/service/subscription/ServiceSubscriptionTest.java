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
import school.faang.user_service.filter.subscription.SubscriptionUsernameFilter;
import school.faang.user_service.repository.SubscriptionRepository;

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
public class ServiceSubscriptionTest {

    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;
    private static final long SAME_ID = 3L;
    private static final long EXPECTED_COUNT = 7L;

    @Mock
    private SubscriptionRepository repository;

    private final SubscriptionFilterDto filterDto = new SubscriptionFilterDto("name2", "000", 1, 10);
    private final List<SubscriptionFilter> subscriptionFilters = List.of(new SubscriptionUsernameFilter());
    private SubscriptionService service;

    @BeforeEach
    void setUp() {
        service = new SubscriptionService(repository, subscriptionFilters);
    }

    @Test
    public void testFollowUserValidVariant() {
        when(repository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(false);

        service.followUser(FOLLOWER_ID, FOLLOWEE_ID);

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(repository, times(1)).save(captor.capture());

        Subscription savedSub = captor.getValue();
        assertEquals(FOLLOWER_ID, savedSub.getFollower_id());
        assertEquals(FOLLOWEE_ID, savedSub.getFollowee_id());
    }

    @Test
    public void testFollowUserInvalidSameUserId() {
        assertThrows(DataValidationException.class, () -> service.followUser(SAME_ID, SAME_ID));
    }

    @Test
    public void testFollowUserInvalidSubAlreadyExists() {
        when(repository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> service.followUser(FOLLOWER_ID, FOLLOWEE_ID));
    }

    @Test
    public void testUnfollowUserValidVariant() {
        when(repository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(true);
        when(repository.getSubscription(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(Subscription.builder()
                .follower_id(FOLLOWER_ID)
                .followee_id(FOLLOWEE_ID)
                .build());

        service.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(repository, times(1)).delete(captor.capture());

        Subscription savedSub = captor.getValue();
        assertEquals(FOLLOWER_ID, savedSub.getFollower_id());
        assertEquals(FOLLOWEE_ID, savedSub.getFollowee_id());
    }

    @Test
    public void testUnfollowUserInvalidSameUserId() {
        assertThrows(DataValidationException.class, () -> service.unfollowUser(SAME_ID, SAME_ID));
    }

    @Test
    public void testUnfollowUserInvalidSubAlreadyExists() {
        when(repository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> service.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID));
    }

    @Test
    public void testGetFollowersCount() {
        when(repository.findFollowersAmountByFolloweeId(FOLLOWEE_ID)).thenReturn(EXPECTED_COUNT);
        Long actualCount = service.getFollowersCount(FOLLOWEE_ID);

        assertEquals(EXPECTED_COUNT, actualCount);
        verify(repository, times(1)).findFollowersAmountByFolloweeId(FOLLOWEE_ID);
    }

    @Test
    public void testGetFolloweesCount() {
        when(repository.findFolloweesAmountByFollowerId(FOLLOWER_ID)).thenReturn(EXPECTED_COUNT);
        Long actualCount = service.getFollowingCount(FOLLOWER_ID);

        assertEquals(EXPECTED_COUNT, actualCount);
        verify(repository, times(1)).findFolloweesAmountByFollowerId(FOLLOWER_ID);
    }

    @Test
    void getFollowersShouldApplyFilters() {
        User user1 = User.builder().username("name1").phone("111").experience(10).build();
        User user2 = User.builder().username("name2").phone("222").experience(1).build();
        User user3 = User.builder().username("name3").phone("111").experience(100).build();
        List<User> allUsers = Arrays.asList(user1, user2, user3);
        List<User> expectedUsers = Collections.singletonList(user2);

        when(repository.findByFolloweeId(anyLong())).thenReturn(allUsers.stream());

        List<User> result = service.getFollowers(FOLLOWEE_ID, filterDto);

        assertEquals(expectedUsers, result);
        verify(repository).findByFolloweeId(FOLLOWEE_ID);
    }

}
