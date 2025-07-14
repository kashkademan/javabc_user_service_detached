package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.validator.user.SubscriptionValidator;
import school.faang.user_service.validator.user.UserValidator;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private FollowerEventPublisher followerEventPublisher;

    @Mock
    private UserValidator userValidator;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void testFollowUserSuccessful() {
        Long followerId = 1L;
        Long followeeId = 2L;

        subscriptionService.followUser(followerId, followeeId);

        verify(userValidator).validate(followerId);
        verify(userValidator).validate(followeeId);
        verify(subscriptionValidator).validate(argThat(data ->
                data.followerId().equals(followerId) && data.followeeId().equals(followeeId)));
        verify(followerEventPublisher).publish(argThat(event ->
                event.getFollowerId().equals(followerId)
                        && event.getFolloweeId().equals(followeeId)
                        && event.getTimestamp() != null));
    }

    @Test
    void testFollowUserValidationFails() {
        Long followerId = 1L;
        Long followeeId = 2L;

        doThrow(new IllegalArgumentException("User not found")).when(userValidator).validate(followerId);

        try {
            subscriptionService.followUser(followerId, followeeId);
        } catch (IllegalArgumentException e) {
            // Expected
        }

        verify(userValidator).validate(followerId);
        verify(userValidator, never()).validate(followeeId);
        verify(subscriptionValidator, never()).validate(any());
        verify(followerEventPublisher, never()).publish(any());
    }
}