package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.user.SubscriptionValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionValidatorTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionValidator subscriptionValidator;

    @Test
    void testValidateSubscriptionDoesNotExist() {
        Long followerId = 1L;
        Long followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        assertDoesNotThrow(() -> subscriptionValidator.validate(new SubscriptionValidator
                .SubscriptionValidationData(followerId, followeeId)));

        verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Test
    void testValidateSubscriptionExists() {
        Long followerId = 1L;
        Long followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionValidator.validate(new SubscriptionValidator
                        .SubscriptionValidationData(followerId, followeeId)));
        assertEquals("Subscription already exists between follower " + followerId + " and followee "
                + followeeId, exception.getMessage());

        verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }
}