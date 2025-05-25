package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.FollowEventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.messaging.EventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventPublisher<FollowEventDto> eventPublisher;
    @InjectMocks
    private SubscriptionServiceImpl service;

    @Test
    public void testFollowUser() {
        long followerId = 1L;
        long followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        doNothing().when(eventPublisher).publish(any());

        service.followUser(followerId, followeeId);
        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    public void testThrowExceptionFollowUserThemselves() {
        long id = 1L;

        assertThrows(DataValidationException.class, () -> service.followUser(id, id));
    }

    @Test
    public void testThrowExceptionFollowUserByExistingFollowing() {
        long followerId = 1L;
        long followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> service.followUser(followerId, followeeId));
    }

    @Test
    public void testUnfollowUser() {
        long followerId = 1L;
        long followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        service.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testThrowExceptionUnfollowUserThemselves() {
        long id = 1L;

        assertThrows(DataValidationException.class, () -> service.followUser(id, id));
    }

    @Test
    public void testThrowExceptionUnfollowUserByNoExistingFollowing() {
        long followerId = 1L;
        long followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> service.unfollowUser(followerId, followeeId));
    }

    @Test
    public void testGetFollowersCount() {
        long id = 1L;

        service.getFollowersCount(id);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(id);
    }

    @Test
    public void testGetFollowingCount() {
        long id = 1L;

        service.getFollowingCount(id);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(id);
    }
}