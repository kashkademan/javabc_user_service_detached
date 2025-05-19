package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.FollowEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.messaging.EventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    private User user1;
    private User user2;

    @BeforeEach
    void init() {
        user1 = User.builder().id(1L).followees(new ArrayList<>()).followers(new ArrayList<>()).build();
        user2 = User.builder().id(2L).followees(new ArrayList<>()).followers(new ArrayList<>()).build();
    }

    @Test
    public void testFollowUser() {
        long followerId = 1L;
        long followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
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